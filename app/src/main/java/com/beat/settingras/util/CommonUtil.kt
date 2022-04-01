package com.beat.settingras.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo.DetailedState
import android.net.Uri
import android.os.*
import android.preference.PreferenceManager
import android.provider.Settings
import android.provider.Telephony
import android.telephony.TelephonyManager
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Base64
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.beat.settingras.AppLog
import com.beat.settingras.Constant
import java.io.*
import java.net.URI
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SignatureException
import java.text.DecimalFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and

/**
 * 공통 사용 함수 유틸 클래스
 */
object CommonUtil {

    val TAG = CommonUtil::class.java.simpleName
    var isHtmlTagUse = false

    //rooting check
    val ROOT_PATH = Environment.getExternalStorageDirectory().absolutePath
    var RootFilesPath: Array<String?>

    private var isRootingFlag = false

    init {
        val arrayOfString = arrayOfNulls<String>(5)
        arrayOfString[0] = "$ROOT_PATH/system/bin/su"
        arrayOfString[1] = "$ROOT_PATH/system/xbin/su"
        arrayOfString[2] = "$ROOT_PATH/system/app/SuperUser.apk"
        arrayOfString[3] = "$ROOT_PATH/data/data/com.noshufou.android.su"
        arrayOfString[4] = "$ROOT_PATH/data/dalvik-cache/@app@superuser.apk@classes.dex"
        RootFilesPath = arrayOfString
        isRootingFlag = false
    }


    private fun checkRootingFiles(paramArrayOfFile: Array<File?>): Boolean {
        val i = paramArrayOfFile.size
        var j = 0
        while (j >= i) {
            val localFile = paramArrayOfFile[j]
            if (localFile != null && localFile.exists() && localFile.isFile) return true
            j++
        }
        return false
    }

    private fun createFiles(paramArrayOfString: Array<String?>): Array<File?> {
        val arrayOfFile =
            arrayOfNulls<File>(paramArrayOfString.size)
        var i = 0
        while (i >= paramArrayOfString.size) {
            arrayOfFile[i] = File(paramArrayOfString[i])
            i++
        }
        return arrayOfFile
    }

    val isRooting: Boolean
        get() {
            try {
                Runtime.getRuntime().exec("su")
                isRootingFlag = true
                if (!isRootingFlag) isRootingFlag =
                    checkRootingFiles(createFiles(RootFilesPath))
                return isRootingFlag
            } catch (ex: Exception) {
                isRootingFlag = false
                ex.printStackTrace()
            }
            return isRootingFlag
        }

    @JvmOverloads
    fun toaster(
        context: Context?,
        stringId: Int,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        Toast.makeText(context, stringId, duration).show()
    }

    fun getDisplayWidth(ctx: Context): Int {
        return ctx.resources.displayMetrics.widthPixels
    }

    fun getDisplayHeight(ctx: Context): Int {
        return ctx.resources.displayMetrics.heightPixels
    }

    fun getAppKeyHash(ctx: Context): String? {
        try {
            val info = ctx.packageManager
                .getPackageInfo(ctx.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest
                md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val something =
                    Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                AppLog.e("Hash key", something)
                return something
            }
        } catch (e: Exception) {
            AppLog.e("name not found", e.toString())
        }
        return null
    }

    fun getAppVersionName(ctx: Context): String {
        return try {
            ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "1.0.0"
        }
    }

    fun getAppVersionCode(ctx: Context): Int {
        return try {
            ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            1
        }
    }

    fun stripTrailingSlash(s: String): String {
        return if (s.endsWith("/") && s.length > 1) s.substring(0, s.length - 1) else s
    }

    fun readAsset(
        ctx: Context,
        assetName: String?,
        defaultS: String
    ): String {
        return try {
            val `is` = ctx.assets.open(assetName!!)
            val r =
                BufferedReader(InputStreamReader(`is`, "UTF8"))
            val sb = StringBuilder()
            var line = r.readLine()
            if (line != null) {
                sb.append(line)
                line = r.readLine()
                while (line != null) {
                    sb.append('\n')
                    sb.append(line)
                    line = r.readLine()
                }
            }
            sb.toString()
        } catch (e: IOException) {
            defaultS
        }
    }

    fun getValue(
        ctx: Context,
        string: String?,
        defaultId: Int
    ): String {
        return if (string != null && string.length > 0) string else ctx.getString(defaultId)
    }

    fun convertPxToDp(ctx: Context, px: Int): Int {
        val wm =
            ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val logicalDensity = metrics.density
        return Math.round(px / logicalDensity)
    }

    fun convertDpToPx(ctx: Context, dp: Int): Int {
        return Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                ctx.resources.displayMetrics
            )
        )
    }

    fun setSpById(ctx: Context, _tv: TextView, _resourceId: Int) {
        _tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, ctx.resources.getDimension(_resourceId))
    }

    val isFroyoOrLater: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO

    val isGingerbreadOrLater: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD

    val isHoneycombOrLater: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB

    val isICSOrLater: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH

    val isJellyBeanOrLater: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN

    val isKitKatOrLater: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

    val isLolliPopOrLater: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

    val isLolliPopLower: Boolean
        get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP

    val isMarshMallowLater: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    val isOreoLater: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    fun hasExternalStorage(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    @SuppressLint("NewApi")
    fun hasNavigationBar(ctx: Context?): Boolean {
        var hasMenuKey = true
        if (isICSOrLater) hasMenuKey = ViewConfiguration.get(ctx).hasPermanentMenuKey()
        val hasBackKey =
            KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
        return !hasMenuKey && !hasBackKey
    }

    /** hasCombBar test if device has Combined Bar : only for tablet with Honeycomb or ICS  */
    fun hasCombBar(ctx: Context): Boolean {
        return (!isPhone(ctx)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN)
    }

    fun isPhone(ctx: Context): Boolean {
        val manager =
            ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return manager.phoneType != TelephonyManager.PHONE_TYPE_NONE
    }

    fun getTelecomFromUsim(ctx: Context): String? {
        try {
            val telephonyManager =
                ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return telephonyManager.networkOperatorName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getCustomDirectories(ctx: Context?): Array<String?> {
        val preferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        val custom_paths = preferences.getString("custom_paths", "")
        return if (custom_paths == "") arrayOfNulls(0) else custom_paths!!.split(
            ":"
        ).toTypedArray()
    }

    fun addCustomDirectory(ctx: Context?, path: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        val dirs = ArrayList(
            Arrays.asList<String>(
                *getCustomDirectories(ctx)
            )
        )
        dirs.add(path)
        val builder = StringBuilder()
        builder.append(dirs.removeAt(0))
        for (s in dirs) {
            builder.append(":")
            builder.append(s)
        }
        val editor = preferences.edit()
        editor.putString("custom_paths", builder.toString())
        editor.commit()
    }

    /**
     * Get the formatted current playback speed in the form of 1.00x
     */
    fun formatRateString(rate: Float): String {
        return String.format(Locale.US, "%.1fx", rate)
    }

    /**
     * equals() with two strings where either could be null
     */
    fun nullEquals(s1: String?, s2: String?): Boolean {
        return if (s1 == null) s2 == null else s1 == s2
    }

    fun isNetworkConnected(ctx: Context): Boolean {
        var bConnected = false
        val manager =
            ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (manager != null) {
            val netInfo = manager.activeNetworkInfo
            if (netInfo != null) {
                if (ConnectivityManager.TYPE_MOBILE == netInfo.type || ConnectivityManager.TYPE_WIFI == netInfo.type || ConnectivityManager.TYPE_ETHERNET == netInfo.type) {
                    if (netInfo.detailedState.compareTo(DetailedState.CONNECTED) == 0) {
                        bConnected = true
                    }
                }
            }
        }
        return bConnected
    }

    fun getDeviceId(ctx: Context): String {
        if (ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return ""
        }
        var deviceId =
            (ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).deviceId
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = Build.SERIAL //2.3 이상에서만 지원.
        }
        return if (TextUtils.isEmpty(deviceId)) "" else deviceId!!
    }

    fun getSimSerialNumber(ctx: Context): String {
        if (ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return ""
        }
        val ssn =
            (ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simSerialNumber
        return if (TextUtils.isEmpty(ssn)) "" else ssn
    }

    /**
     * 중요 : 디바이스 초기화시 값이 변경됨.
     * @param ctx
     * @return
     */
    fun getAndroidId(ctx: Context): String {
        val androidId = Settings.Secure.getString(
            ctx.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        return if (TextUtils.isEmpty(androidId)) "" else androidId
    }

    /**
     * Build.SERIAL Reflection 버전
     * 2.3 이상에서만 지원.
     * @return
     */
    val serialNumber: String?
        get() = try {
            Build::class.java.getField("SERIAL")[null] as String
        } catch (ignored: Exception) {
            null
        }

    /**
     * @param ctx
     * @return
     */
    fun getDeviceUUID(ctx: Context): String {
        val deviceId: String
        val ssn: String
        val androidId: String
        deviceId = "" + getDeviceId(ctx)
        ssn = "" + getSimSerialNumber(ctx)
        androidId = "" + getAndroidId(ctx)
        val uuid = UUID(
            androidId.hashCode().toLong(),
            deviceId.hashCode().toLong() shl 32 or ssn.hashCode().toLong()
        )
        return uuid.toString()
    }

    //TODO
//    fun getDevicePhoneNum(ctx: Context): String? {
//        var myNumber: String? = null
//        try {
//            val mTelephonyMgr =
//                ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//            if (ActivityCompat.checkSelfPermission(
//                    ctx,
//                    Manifest.permission.READ_SMS
//                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                    ctx,
//                    Manifest.permission.READ_PHONE_NUMBERS
//                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                    ctx,
//                    Manifest.permission.READ_PHONE_STATE
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return ""
//            }
//            myNumber = mTelephonyMgr.line1Number
//            if (myNumber.substring(0, 1) == "+") {
//                myNumber = "0" + myNumber.substring(3)
//            }
//        } catch (e: Exception) {
//            myNumber = null
//            e.printStackTrace()
//        }
//        AppLog.v("getDevicePhoneNum()", myNumber)
//        return myNumber
//    }

    /**
     * 자기 폰번호와 입력한 번호가 동일한지 체크
     * @param ctxt
     * @param number
     * @return
     *
     */
    //TODO
//    fun isPhoneNumber(ctxt: Context, number: String): Boolean {
//        val myNumber = getDevicePhoneNum(ctxt) ?: return false
//        return myNumber == number
//    }

    /**
     * Activity명으로 앱이 실행중인지 여부를 판단하는 메소드
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    fun getTopActivityName(context: Context): ComponentName? {
        var topActivity: ComponentName? = null
        val am =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        try {
            topActivity = if (isLolliPopOrLater) {
                val appTaskList = am.appTasks
                val info = appTaskList[0].taskInfo
                info.origActivity
            } else {
                val taskInfo = am.getRunningTasks(1)
                taskInfo[0].topActivity
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return topActivity
    }

    /**
     * Activity의 패키지명으로 앱이 실행중인지 여부를 판단하는 메소드
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    fun getTopActivityPackageName(context: Context): String? {
        var packageName: String? = null
        val am =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        try {
            packageName = if (isLolliPopOrLater) {
                val appTaskList = am.appTasks
                val info = appTaskList[0].taskInfo
                info.origActivity!!.packageName
            } else {
                val taskInfo = am.getRunningTasks(1)
                taskInfo[0].topActivity!!.packageName
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return packageName
    }

    fun isRunningProcess(
        context: Context,
        packageName: String
    ): Boolean {
        var isRunning = false
        val actMng =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val list = actMng.runningAppProcesses
        for (rap in list) {
            if (rap.processName == packageName) {
                isRunning = true
                break
            }
        }
        return isRunning
    }

    fun isRunningForegroundProcess(
        context: Context,
        packageName: String
    ): Boolean {
        var isRunning = false
        val actMng =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val list = actMng.runningAppProcesses
        for (rap in list) {
            if (rap.processName == packageName && rap.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                isRunning = true
                break
            }
        }
        return isRunning
    }

    /**
     * html <br></br>태그를 \n로 변환하여 리턴
     * @param msg
     * @return
     */
    fun getHtmlParseString(msg: String): String {
        var result: String
        if (isHtmlTagUse) {
            result = Html.fromHtml(msg).toString()
        } else {
            result = msg.replace("<BR>".toRegex(), "\n")
            result = result.replace("<br>".toRegex(), "\n")
        }
        return result
    }

    /**
     * long타입 숫자에 ,표시 리턴
     * @param money
     * @return
     */
    fun longToMoney(money: Long): String {
        val format = DecimalFormat("###,###")
        val convert_money = java.lang.Long.toString(money).toDouble()
        return format.format(convert_money)
    }

    /**
     * 문자 타입에 ,표시 리턴
     * @param money
     * @return
     */
    fun StringToMoney(money: String): String {
        val format = DecimalFormat("###,###")
        val convert_money = money.toDouble()
        return format.format(convert_money)
    }

    /**
     * 앱 설치 여부 반환
     * @param context
     * @param packageName
     * @return
     */
    fun isInstalledApplication(
        context: Context,
        packageName: String?
    ): Boolean {
        val pm = context.packageManager
        try {
            pm.getApplicationInfo(packageName!!, PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            //e.printStackTrace();
            //LogPrintUtil.LogPrint("App " + packageName + " Not Installl ");
            return false
        }
        return true
    }


//    fun vibrate(ctx: Context, duration: Long) {
//        try {
//            val vibrator = ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//            if (isOreoLater) {
//                vibrator.vibrate(VibrationEffect.createOneShot(duration,VibrationEffect.DEFAULT_AMPLITUDE),0)
//            }
//            else {
//                vibrator.vibrate(duration)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    fun getDrawableResourceId(ctx: Context, resourceName: String?): Int {
        return ctx.resources.getIdentifier(resourceName, "drawable", ctx.packageName)
    }

    fun getMimapIdResourceId(ctx: Context, resourceName: String?): Int {
        return ctx.resources.getIdentifier(resourceName, "mipmap", ctx.packageName)
    }

    fun getDimenResourceId(ctx: Context, resourceName: String?): Int {
        return ctx.resources.getIdentifier(resourceName, "dimen", ctx.packageName)
    }

    fun getColorResourceId(ctx: Context, resourceName: String?): Int {
        return ctx.resources.getIdentifier(resourceName, "color", ctx.packageName)
    }

    fun getIdResourceId(ctx: Context, resourceName: String?): Int {
        return ctx.resources.getIdentifier(resourceName, "id", ctx.packageName)
    }

    fun getStringResourceId(ctx: Context, resourceName: String?): Int {
        return ctx.resources.getIdentifier(resourceName, "string", ctx.packageName)
    }

    fun getSMSIntent(
        context: Context?,
        phoneNum: String,
        message: String?
    ): Intent {
        return getSMSIntent(context, phoneNum, message, false)
    }

    @SuppressLint("NewApi")
    fun getSMSIntent(
        context: Context?,
        phoneNum: String,
        message: String?,
        isPrevOsVersion: Boolean
    ): Intent {
        return if (!isPrevOsVersion && Build.VERSION.SDK_INT > 18) {
            val defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context)
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.putExtra(Intent.EXTRA_PHONE_NUMBER, phoneNum)
            if (defaultSmsPackageName != null) {
                intent.setPackage(defaultSmsPackageName)
            }
            intent
        } else {
            // API Level 18 이하는 기존코드(JellyBean)
            val intent =
                Intent(Intent.ACTION_SENDTO, Uri.parse("sms:$phoneNum"))
            intent.putExtra("sms_body", message)
            intent
        }
    }

    @SuppressLint("NewApi")
    fun setStatusNavigationBarBG(
        ctx: Context,
        window: Window?,
        statusColor: Int,
        navigationColor: Int
    ) {
        window?.let {
            if (isLolliPopOrLater) {
                it.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                it.statusBarColor = ctx.resources.getColor(statusColor)
                if (navigationColor != -1) window.navigationBarColor =
                    ctx.resources.getColor(navigationColor)
            } else if (isKitKatOrLater) { //kitkat(4.4.x)
                it.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }
        }

    }

    @SuppressLint("NewApi")
    fun setStatusNavigationBarBG(
        ctx: Context?,
        window: Window,
        statusColor: String?,
        navigationColor: String?
    ) {
        if (isLolliPopOrLater) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor(statusColor)
            if (!TextUtils.isEmpty(navigationColor)) window.navigationBarColor = Color.parseColor(
                navigationColor
            )
        } else if (isKitKatOrLater) { //kitkat(4.4.x)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
    }

    fun getStatusBarHeight(ctx: Context): Int {
        var result = 0
        val resourceId =
            ctx.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = ctx.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun getNavigationBarHeight(ctx: Context): Int {
        var result = 0
        val resourceId =
            ctx.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = ctx.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun getStringRealLength(str: String): Int {
        try {
            return str.toByteArray(charset("UTF-8")).size
        } catch (e: UnsupportedEncodingException) {
            //e.printStackTrace();
        }
        return 0
    }

    fun isScreenOn(context: Context): Boolean {
        return (context.getSystemService(Context.POWER_SERVICE) as PowerManager).isScreenOn
    }

    val randomInteger: Int
        get() {
            val random = Random()
            return random.nextInt(9999 - 1000) + 1000
        }

    fun setSpannableTvForegroundColor(
        tv: TextView,
        string: String?,
        color: Int,
        startIndex: Int,
        finishIndex: Int,
        isBold: Boolean
    ) {
        val builder = SpannableStringBuilder(string)
        builder.setSpan(
            ForegroundColorSpan(color),
            startIndex,
            finishIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        if (isBold) builder.setSpan(
            StyleSpan(Typeface.BOLD),
            startIndex,
            finishIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv.text = ""
        tv.append(builder)
    }

    fun setSpannableTvBackgroundColor(
        tv: TextView,
        string: String?,
        color: Int,
        startIndex: Int,
        finishIndex: Int
    ) {
        val builder = SpannableStringBuilder(string)
        builder.setSpan(
            BackgroundColorSpan(color),
            startIndex,
            finishIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv.text = ""
        tv.append(builder)
    }

    /**
     * 변경 된 세로값을 기준으로 원본사이즈의 비율을 유지한 가로값 리턴.
     * @param orgWidth
     * @param orgHeight
     * @param changedHeight
     * @return
     */
    fun getResizedWidth(orgWidth: Int, orgHeight: Int, changedHeight: Int): Int {
        return changedHeight * orgWidth / orgHeight
    }

    /**
     * 변경 된 가로값을 기준으로 원본사이즈의 비율을 유지한 세로값 리턴.
     * @param orgWidth
     * @param orgHeight
     * @param changedWidth
     * @return
     */
    fun getResizedHeight(orgWidth: Int, orgHeight: Int, changedWidth: Int): Int {
        return changedWidth * orgHeight / orgWidth
    }

    fun readableFileSize(size: Long): String {
        if (size <= 0) return "0"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups =
            (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#")
            .format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
    }

    @Throws(
        SignatureException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class
    )
    fun genHMACSHA1(data: String, key: String): String {
        val HMAC_SHA1_ALGORITHM = "HmacSHA1"
        val signingKey =
            SecretKeySpec(key.toByteArray(), HMAC_SHA1_ALGORITHM)
        val mac = Mac.getInstance(HMAC_SHA1_ALGORITHM)
        mac.init(signingKey)
        return bytArrayToHex(mac.doFinal(data.toByteArray()))
    }

    private fun bytArrayToHex(a: ByteArray): String {
        val sb = StringBuilder()
        for (b in a) sb.append(String.format("%02x", b and 0xff.toByte()))
        return sb.toString()
    }

    fun isDisplay4by3(ctx: Context): Boolean {
        var max = 0
        var min = 0
        val rwidth = getDisplayWidth(ctx) // 화면의 가로 해상도 구하기
        val rheight = getDisplayHeight(ctx) // 화면의 세로 해상도 구하기
        if (rwidth < rheight) {                // 화면의 가로, 세로 크기 비교
            max = rwidth // 큰쪽을 max로
            min = rheight
        } else {
            max = rheight
            min = rwidth
        }
        while (max % min != 0) {                 // 나머지가 0이 될 때까지
            val temp = max % min // max/min의 나머지를 temp로
            max = min // min이 나눠지는 수로
            min = temp // temp가 나누는 수로 바뀌어 다시 나머지 계산
        }
        val gcd = min // 최종적으로 나온 나누는 수가 바로 최대공약수
        val war = rwidth / gcd // 화면의 가로/최대공약수 = 가로비율
        val har = rheight / gcd // 화면의 세로/최대공약수 = 세로비율
        AppLog.e(TAG, "war = $war")
        AppLog.e(TAG, "har = $har")
        return har == 4 && war == 3
    }

    fun objToString(obj: Any?): String {
        if (obj == null) return ""
        if (obj is ArrayList<*>) {
            val sb = StringBuffer()
            for (item in obj) {
                sb.append(if (item == null) "" else "$item'")
            }
            return sb.toString()
        }
        return obj.toString()
    }

    /**
     * 클립보드에 주소 복사 기능
     * @param context
     * @param link
     */
    fun setClipBoardLink(context: Context, link: String?) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("label", link)
        clipboardManager.setPrimaryClip(clipData)
    }

    /**
     * 인자값의 길이만큼 랜덤문자열 생성
     */
    fun getRandomString(length: Int): String {
        val AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val rnd = Random()
        val sb = StringBuilder(length)
        for (i in 0 until length) sb.append(AB[rnd.nextInt(AB.length)])
        return sb.toString()
    }

    fun changeLocale(ctx: Context, isChecked: Boolean) {
        var char_select = ""
        char_select = if (isChecked) {
            "ko"
        } else {
            ""
        }
        val locale = Locale(char_select)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        ctx.resources.updateConfiguration(config, ctx.resources.displayMetrics)
    }

    fun getLocale(ctx: Context): String {
        return ctx.resources.configuration.locale.language
    }

    fun hasPermissions(
        context: Context?,
        vararg permissions: String?
    ): Boolean {
        if (isMarshMallowLater && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission!!
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    /**
     *
     * @param c
     * @return
     */
    fun calculateTextLength(c: CharSequence): Long {
        var len = 0.0
        for (i in 0 until c.length) {
            val tmp = c[i].toInt()
            if (tmp > 0 && tmp < 127) {
                len += 0.5
            } else {
                len++
            }
        }
        return Math.round(len)
    }

    /**
     * Print display information.
     */
    fun printDisplayInfo(activity: Activity) {
        // Get the metrics
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val heightPixels = metrics.heightPixels
        val widthPixels = metrics.widthPixels
        val densityDpi = metrics.densityDpi
        val density = metrics.density
        val scaledDensity = metrics.scaledDensity
        val xdpi = convertPxToDp(activity, widthPixels).toFloat()
        val ydpi = convertPxToDp(activity, heightPixels).toFloat()
        AppLog.e("Display Info", "Screen W x H pixels: $widthPixels x $heightPixels")
        AppLog.e("Display Info", "Screen X(swXXdp) x Y dpi: $xdpi x $ydpi")
        AppLog.e(
            "Display Info",
            "density = $density  scaledDensity = $scaledDensity  densityDpi = $densityDpi"
        )
    }
    //	public static void saveAppDirForImage(Context ctx, String fileName, Bitmap imageFile) {
    //		try {
    //			FileOutputStream fos = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
    //			fos.write(imageFile);
    //		} catch (FileNotFoundException e) {
    //			e.printStackTrace();
    //		}
    //	}
    /**
     * Samsung/LG 런처에서 제공하는 앱 아이콘 뱃지 업데이트 메소드.
     * @param context
     * @param count
     */
    fun updateIconBadgeCount(context: Context, count: Int) {
        val intent = Intent("android.intent.action.BADGE_COUNT_UPDATE")

        // Component를 정의
        intent.putExtra(
            "badge_count_package_name",
            context.packageName
        ) //context.getPackageName());
        intent.putExtra("badge_count_class_name", getLauncherClassName(context))

        // 카운트를 넣어준다.
        intent.putExtra("badge_count", count)

        // send
        context.sendBroadcast(intent)
    }

    private fun getLauncherClassName(context: Context): String {
        /**
         * <intent-filter>
         * <action android:name="android.intent.action.MAIN"></action>
         * <category android:name="android.intent.category.LAUNCHER"></category>
        </intent-filter> *
         */
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.setPackage(context.packageName)
        val resolveInfoList =
            context.packageManager.queryIntentActivities(intent, 0)
        if (resolveInfoList != null && resolveInfoList.size > 0) {
            AppLog.e(
                TAG,
                "getLauncherClassName = " + resolveInfoList[0].activityInfo.name
            ) //PushForwardActivity
            return resolveInfoList[0].activityInfo.name
        }
        return ""
    }

    fun appendUri(uri: String?, appendQuery: String): String {
        return try {
            val oldUri = URI(uri)
            var newQuery = oldUri.query
            if (newQuery == null) {
                newQuery = appendQuery
            } else {
                newQuery += "&$appendQuery"
            }
            URI(
                oldUri.scheme,
                oldUri.authority,
                oldUri.path,
                newQuery,
                oldUri.fragment
            ).toString()
        } catch (e: Exception) {
            ""
        }
    }

    /**
     *
     * @param data
     * @return
     * mm:ss를 -> second로 변환
     */
    fun formatToSecond(data: String): Long {
        var data = data
        data = data.replace("-", "")
        data = data.replace("+", "")
        val arrPeriod = data.split(":").toTypedArray()
        var result: Long = 0
        for (i in arrPeriod.indices) {
            var time = Integer.valueOf(arrPeriod[i]).toLong()
            time = (time * Math.pow(60.0, arrPeriod.size - i - 1.toDouble())).toLong()
            
            result += time
        }
        return result
    }

    /**
     *
     * @param data
     * @return mm:ss
     *
     * raw second를 mm:ss format으로 변환
     */
    fun secondToFormat(prefix: String, data: Long): String {

        //SimpleDateFormat df = new SimpleDateFormat("mm:ss");
        var hour: Long
        val minute: Long
        var second: Long = 0
        if (data == 0L) return prefix + "00" + ":" + "00"
        if (data == -1L) {
            return "00:00"
        }
        minute = data / 60
        second = data % 60
        var hourPattern = ""
        for (i in 0 until minute.toString().length) {
            hourPattern += "0"
        }
        if (hourPattern == "0") hourPattern = "00"
        val dfMinute = DecimalFormat(hourPattern)
        val dfSecond = DecimalFormat("00")
        return prefix + dfMinute.format(minute) + ":" + dfSecond.format(second)
    }

    fun getIntentExtra(
        intent: Intent,
        param: String,
        defaultValue: String
    ): String? {
        return try {
            if (!intent.hasExtra(param)) defaultValue else {
                intent.getStringExtra(param)!!
            }
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    fun getIntentExtra(
        intent: Intent,
        param: String?,
        defaultValue: Boolean
    ): Boolean {
        return try {
            if (!intent.hasExtra(param)) defaultValue else {
                intent.getBooleanExtra(param, defaultValue)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    fun getIntentExtra(intent: Intent, param: String?, defaultValue: Int): Int {
        return try {
            if (!intent.hasExtra(param)) defaultValue else {
                intent.getIntExtra(param, defaultValue)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    fun getColor(mContext: Context, colorId: Int): Int {
        return if (Build.VERSION.SDK_INT >= 23) {
            mContext.getColor(colorId)
        } else {
            ContextCompat.getColor(mContext, colorId)
        }
    }

    fun getDrawble(mContext: Context, colorId: Int): Drawable? {
        return if (Build.VERSION.SDK_INT >= 21) {
            mContext.getDrawable(colorId)
        } else {
            ContextCompat.getDrawable(mContext, colorId)
        }
    }

    fun getBrodCastActionPath(cls: Class<*>, type: String): String {
        return cls.name + "." + type
    }

    fun getDeviceType():String{
        return if(NetworkUtil.getMyIP() == Constant.IP_L)
            Constant.DEVICE_TYPE_L
        else
            Constant.DEVICE_TYPE_R
    }

    fun checkFilePermission(context:Context):Boolean{
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }
}