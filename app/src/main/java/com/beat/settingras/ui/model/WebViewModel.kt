package com.beat.settingras.ui.model

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import android.webkit.*
import androidx.lifecycle.MutableLiveData
import com.beat.settingras.AppLog
import com.beat.settingras.BuildConfig
import com.beat.settingras.Constant
import com.beat.settingras.data.remote.source.API
import com.beat.settingras.data.remote.source.repository.RemoteBoothRepository
import com.beat.settingras.data.remote.source.repository.RemoteCommonRepository
import com.beat.settingras.ui.BaseViewModel
import com.beat.settingras.ui.webview.MyWebChromeClient
import com.beat.settingras.ui.webview.MyWebViewClient
import com.beat.settingras.util.CommonUtil
import java.util.*

class WebViewModel(
    private val commonRepository: RemoteCommonRepository,
    private val boothRepository: RemoteBoothRepository
) : BaseViewModel() {

    val jsAlert = MutableLiveData<HashMap<String, Any?>>()
    val dataInit = MutableLiveData<HashMap<String, Any?>>()
    val orderIntent = MutableLiveData<Intent>()
    val errorUrl = MutableLiveData<String>()

    var restoreHandler: Handler?=null

    inner class HybridJavaScriptBirdge {
        @JavascriptInterface
        fun toast(msg: String) {
            toast.value = msg
        }

        @JavascriptInterface
        fun finish() {
            finish.value = true
        }
    }

    inner class TeleditBridge {
        /**
         * 휴대폰 결제 완료 후 결과값 전달 받기
         * window.location = "dalkommapp:Result:{{return_code}}|{{return_msg}}|{{uas_name}}|{{uas_sex}}|{{uas_birthday}}";
         *
         * 멤버십 통합 업데이트 이후 변경
         * URI 포맷으로 변경
         * return_code=20000&return_msg=본인인증 성공하였습니다.
         * &uas_name=정유성
         * &uas_sex=1
         * &uas_birthday=19810225
         * &uas_mobile=01073401390
         * &uas_auth_token=e977968c-c1f7-11e8-814c-001a4bd4afe6
         * &uas_user=qqqqwwww (사전에 본인인증한 값이 없으면 키값 자체가 안옴)
         * &uas_user_login_type=D
         */

        @JavascriptInterface
        fun Result(result: String) {
            AppLog.e(TAG, "result  = $result")
            if (result.isNullOrEmpty()) {

            }
        }

        @JavascriptInterface
        fun BestClose() {
            AppLog.e(TAG, "BestClose")
            finish.value = true
        }
    }

    var restoreRunnable:Runnable = object : Runnable{
        override fun run() {
            AppLog.d(TAG,"restoreRunnable")
            errorUrl.postValue(API.URL.BASE_WEB_URL)
            restoreHandler?.postDelayed(this, Constant.REMAIN_WEB_RESTORE_TIME)
        }
    }

    fun setWebViewSetting(webView: WebView) {
        //SETTING
        webView.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            webViewClient = getDefaultWebViewClient()
        }
        webView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(false)
            useWideViewPort = false
            loadWithOverviewMode = false
            domStorageEnabled = true
            allowFileAccess = true
            setAppCacheEnabled(true)
            loadWithOverviewMode = true
            cacheMode = WebSettings.LOAD_DEFAULT
            layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                textZoom = 100

            if (CommonUtil.isJellyBeanOrLater) {
                allowUniversalAccessFromFileURLs = true
                allowFileAccessFromFileURLs = true
            }

            if (CommonUtil.isLolliPopOrLater) {
                webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                val cookieManager =
                    CookieManager.getInstance()
                cookieManager.setAcceptCookie(true)
                cookieManager.setAcceptThirdPartyCookies(webView, true)
            }
        }

        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
    }

    fun setDefaultJavascriptInterface(webView: WebView) {
        webView.addJavascriptInterface(TeleditBridge(), "TeleditApp")
        webView.addJavascriptInterface(HybridJavaScriptBirdge(), "hybrid")
    }

    private fun getDefaultWebViewClient(): WebViewClient {
        return MyWebViewClient(this)
    }

    fun setDefaultChromeWebViewClient(webView: WebView) {
        webView.webChromeClient = MyWebChromeClient(this)
    }

    fun parseScheme(url: String?): Boolean {
        if (url != null && isAvailableParseScheme(url)) {

            try {
                var intent: Intent? = null
                try {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                } catch (e: Exception) {
                    e.printStackTrace()
                    return false
                }


                if (url.startsWith("intent")) {
                    if (intent.`package` != null) {
                        val uri = Uri.parse("market://search?q=panme:${intent.`package`}")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        orderIntent.value = intent
                        return true
                    }

                    val uri = Uri.parse(intent.dataString)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    orderIntent.value = intent

                } else {
                    // 구 방식
                    val uri = Uri.parse(url)
                    intent = Intent(Intent.ACTION_VIEW, uri)
                    orderIntent.value = intent
                }
            } catch (e: Exception) {
                AppLog.e(TAG, "ActivityNotFoundException")
                if (url.startsWith("vguardend://")) {
                    AppLog.e(TAG, "Skip : $url")
                    return true
                }
                return false
            }
        } else {
            //TODO webView.LoadUrl
            return false
        }
        return true
    }


    private fun isAvailableParseScheme(url: String): Boolean {
        return (url.contains("cloudpay")
                || url.contains("hanaansim")
                || url.contains("citispayapp")
                || url.contains("citicardapp")
                || url.contains("mvaccine")
                || url.contains("com.TouchEn.mVaccine.webs")
                || url.contains("market://")
                || url.contains("smartpay")
                || url.contains("com.ahnlab.v3mobileplus")
                || url.contains("droidxantivirus")
                || url.contains("v3mobile")
                || url.endsWith(".apk")
                || url.contains("market://")
                || url.contains("ansimclick")
                || url.contains("market://details?id=com.shcard.smartpay")
                || url.contains("shinhan-sr-ansimclick://")
                || url.contains("http://m.ahnlab.com/kr/site/download")
                || url.contains("com.lotte.lottesmartpay")
                || url.startsWith("lottesmartpay://")
                || url.contains("http://market.android.com")
                || url.contains("smhyundaiansimclick://")
                || url.contains("smshinhanansimclick://")
                || url.contains("smshinhancardusim://")
                || url.contains("smartwall://")
                || url.contains("appfree://")
                || url.startsWith("kb-acp://")
                || url.startsWith("intent://")
                || url.startsWith("ahnlabv3mobileplus")
                || url.contains("smhyundaiansimclick")
                || url.contains("smshinhanansimclick")
                || url.contains("shinhan-sr-ansimclick")
                || url.contains("vguard")
                || url.contains("vguardstart")
                || url.contains("vguardend")
                || url.contains("droidx3host")
                || url.contains("mpocket.online.ansimclick")
                || url.contains("hdcardappcardansimclick")
                || url.contains("nhappcardansimclick")
                || url.contains("nonghyupcardansimclick")
                || url.contains("tswansimclick")
                || url.contains("payco")
                || url.contains("samsungpay"))
    }


    fun getUasReturnMsg(resultArray: Array<String?>?): String? {
        return if (resultArray == null || resultArray.size < 2) "" else resultArray[1]
    }

    fun getUasUriReturnMsg(src: String): String? {
        var src = src

        if (src.isNullOrEmpty()) return ""
        if (!src.startsWith("http"))
            src = "http://www.dalkomm.com/parse?$src" //uri parse를 위한 더미 프로토콜 추가.

        val uri = Uri.parse(src)
        return uri.getQueryParameter("return_msg")
    }

    fun addLangParamsForURL(url: String): String? {
        if (TextUtils.isEmpty(url)) return ""
        return if (url.contains("lang="))
            url
        else
            CommonUtil.appendUri(url, "lang=" + "ko")
    }

    // 웹뷰 에러 시 핸들러
    fun startRestoreWebView(){
        AppLog.l()
        if(restoreHandler == null) {
            AppLog.d(TAG,"restoreHandler null")
            restoreHandler = Handler()
            restoreHandler?.removeCallbacksAndMessages(null)
            restoreHandler?.postDelayed(restoreRunnable, Constant.REMAIN_WEB_RESTORE_TIME)
        }
    }

    fun stopRestoreWebView(){
        AppLog.l()
        if(restoreHandler!=null){
            restoreHandler?.removeCallbacksAndMessages(null)
            restoreHandler = null
        }
    }

    fun isNullRestoreHandler():Boolean {
        return if (restoreHandler == null) true else false
    }

    override fun onCleared() {
        super.onCleared()
        restoreHandler?.let {
            it.removeCallbacksAndMessages(null)
            restoreHandler = null
        }
    }

    companion object {
        val TAG: String = WebViewModel::class.java.simpleName
    }
}