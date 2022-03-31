package com.beat.settingras.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.beat.settingras.AppLog
import com.beat.settingras.Constant
import com.beat.settingras.data.remote.source.API
import com.beat.settingras.data.remote.source.repository.RemoteBoothRepository
import com.beat.settingras.data.remote.source.repository.RemoteCommonRepository
import com.beat.settingras.ui.receiver.AlarmBroadCastReceiver
import com.beat.settingras.ui.receiver.RemainBroadCastReceiver
import com.beat.settingras.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.getKoin
import java.text.SimpleDateFormat
import java.util.*

/**
 * 주기적으로 중앙 서버와 통신하여 업데이트 체크(1시간 내외)
 */
class HealthCheckService() : Service() {

    private val commonRepository: RemoteCommonRepository = getKoin().get()
    private val boothRemoteCommonRepository: RemoteBoothRepository = getKoin().get()

    private lateinit var updateCheckHandler: Handler
    private lateinit var alarmReceiveReceiver: BroadcastReceiver

    private var errCnt: Int = 0
    var updateRunnable = Runnable { requestVersionStatus() }

    inner class AlarmReceiveReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            AppLog.d("AlarmReceiveReceiver")
            if (context != null && intent != null) {
                if (!intent.hasExtra(Constant.KEY.TYPE))
                    return
                if (intent.getIntExtra(Constant.KEY.TYPE, -1) == Constant.ALARM_TYPE_CHECK_IS_UPDATE) {
                    AppLog.d(TAG, "ALARM_TYPE_CHECK_IS_UPDATE")
                    if(CommonUtil.checkFilePermission(applicationContext)){
                        FileUtil.writeIP()
                    }
                    requestBoothId()
                } else if (intent.getIntExtra(
                        Constant.KEY.TYPE,
                        -1
                    ) == Constant.ALARM_TYPE_CHECK_REMAIN
                ) {
                    AppLog.d(TAG, "ALARM_TYPE_CHECK_REMAIN")
                    updateCheckHandler.postDelayed(updateRunnable, 100)
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        AppLog.d(TAG, "service onCreate")

        updateCheckHandler = Handler()
        alarmReceiveReceiver = AlarmReceiveReceiver()
        alarmReceiveReceiver?.let {
            val filter = IntentFilter()
            filter.addAction(Constant.INTENT_ACION.ACTION_ALARM_HEALTH_RECEIVE)
            LocalBroadcastManager.getInstance(this).registerReceiver(it, filter)
        }

        if(CommonUtil.checkFilePermission(applicationContext)){
            FileUtil.writeIP()
        }

        cancelAllAlarm()
        requestBoothId()
        FileUtil.writeIP()
    }


    /**
     * 부스코드 요청
     * Success 아닌 경우 내일 다시 실행
     *
     */
    private fun requestBoothId() {
        boothRemoteCommonRepository.requestBoothId()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {}
            .doOnSuccess {}
            .doOnError {}
            .subscribe({
                // null인 경우 무조건 에러
                if (it == null) {
                    setAlarmTime(
                        applicationContext,
                        Constant.ALARM_TYPE_CHECK_IS_UPDATE,
                        getDefaultCalendar()
                    )
                }
                // 2.0 기준 resultCode 가 0000이고 API resultMsg가 SUCCESS인 경우
                else if (it.resultCode == API.BOOTHRETURN.SUCCESS_CODE &&
                    it.resultMessage == API.BOOTHRETURN.SUCCESS_MSG) {
                    if(!it.boothId.isNullOrEmpty())
                        PreferenceUtil.setString(applicationContext, Constant.PARAM.BOOTH_CODE, it.boothId)
                    requestVersionCheck()
                }
                // 2.0 기준 resultCode 가 SUCCESS 이고 API resultMsg가 SUCCESS인 경우
                else if (it.resultCode == API.BOOTHRETURN.SUCCESS_MSG &&
                    it.resultMessage == API.BOOTHRETURN.SUCCESS_MSG) {
                    if(!it.boothId.isNullOrEmpty())
                        PreferenceUtil.setString(applicationContext, Constant.PARAM.BOOTH_CODE, it.boothId)
                    requestVersionCheck()
                }
                // 알람을 내일 00시로 맞춤
                else {
                    setAlarmTime(
                        applicationContext,
                        Constant.ALARM_TYPE_CHECK_IS_UPDATE,
                        getDefaultCalendar()
                    )
                }

            }, {
                it.printStackTrace()
                requestVersionCheck()
            })
    }

    /**
     *
     */

    private fun requestVersionCheck() {
        val boothCode = PreferenceUtil.getString(applicationContext, Constant.PARAM.BOOTH_CODE)

        if (boothCode.isNullOrEmpty()) {
            AppLog.d(TAG,"boothCode is Empty")
            errCnt = Constant.ERROR_UPDATE_CNT
            setUpdateError()
            setAlarmTime(applicationContext, Constant.ALARM_TYPE_CHECK_IS_UPDATE, getDefaultCalendar())
            return
        }

        val params = HashMap<String, Any?>().apply {
            this[Constant.PARAM.BOOTH_CODE] = boothCode
            this[Constant.PARAM.VERSION_CODE] = CommonUtil.getAppVersionCode(applicationContext)
            this[Constant.PARAM.DEVICE_TYPE] = CommonUtil.getDeviceType()
            this[Constant.PARAM.SECURE_ANDROID_ID] = CommonUtil.getAndroidId(applicationContext)
        }

        commonRepository.requestVersionCheck(params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {}
            .doOnSuccess {}
            .doOnError {}
            .subscribe({
                if (it.meta.code != API.RETURN.SUCCESS) {
                    setAlarmTime(applicationContext, Constant.ALARM_TYPE_CHECK_IS_UPDATE, getDefaultCalendar())
                    return@subscribe
                }

                it.data.serverTime?.let {
                    SysUtils.setSystemDate(it)
                }

                if (it.data.updateAble) {
                    setAlarmTime(applicationContext, Constant.ALARM_TYPE_CHECK_IS_UPDATE, getDefaultCalendar())
                    setAlarmTime(applicationContext, Constant.ALARM_TYPE_CHECK_REMAIN, getAlarmDate(it.data.updateTime))
                    return@subscribe
                }
                setAlarmTime(applicationContext, Constant.ALARM_TYPE_CHECK_IS_UPDATE, getDefaultCalendar())
            }, { e ->
                e.printStackTrace()
                setAlarmTime(applicationContext, Constant.ALARM_TYPE_CHECK_IS_UPDATE, getDefaultCalendar())
            })
    }

    /**
     * 업데이트가 되는지 확인
     * 1분 주기
     * 특정 횟수 초과 시 다음날로 이전
     * 매장코드가 없을 시 마찬가지로 다음날로 이전
     */
    private fun requestVersionStatus() {
        AppLog.l()
        val boothCode = PreferenceUtil.getString(applicationContext, Constant.PARAM.BOOTH_CODE)
        if (boothCode.isNullOrEmpty()) {
            AppLog.d(TAG,"boothCode is Empty")
            errCnt = Constant.ERROR_UPDATE_CNT
            setUpdateError()
            setAlarmTime(applicationContext, Constant.ALARM_TYPE_CHECK_IS_UPDATE, getDefaultCalendar())
            return
        }

        val params = HashMap<String, Any?>().apply {
            this[Constant.PARAM.BOOTH_CODE] = boothCode
            this[Constant.PARAM.VERSION_CODE] = CommonUtil.getAppVersionCode(applicationContext)
            this[Constant.PARAM.DEVICE_TYPE] = CommonUtil.getDeviceType()
            this[Constant.PARAM.SECURE_ANDROID_ID] = CommonUtil.getAndroidId(applicationContext)
        }

        commonRepository.`requestVersionStatus`(params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {}
            .doOnSuccess {}
            .doOnError {}
            .subscribe({
                it.data?.let {
                    if(it.updateAble){
                        clearUpdateCheckHandler()
                        cancelRemainAlarm()
                        SysUtils.reboot()
                        return@subscribe
                    }else{
                        setUpdateError()
                    }
                }
            }, { e ->
                e.printStackTrace()
                setUpdateError()
            })
    }

    //TODO intent의 해당 경우가 있는 경우...알람등록
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            if (intent.hasExtra(Constant.KEY.TYPE)) {
                AppLog.d(TAG, "onStartCommand ${intent.getIntExtra(Constant.KEY.TYPE, -1)}")
                Toast.makeText(
                    applicationContext,
                    "type ${intent.getIntExtra(Constant.KEY.TYPE, -1)}",
                    Toast.LENGTH_LONG
                )
            }
        }
        return START_STICKY
    }

    /**
     * 서비스 시작 시 알람등록 -> 시간존재 -> 시간 저장 -> 알람 제거 및 재생성 -> AlarmReceiver 실행 -> service Intent parameter 전달 -> remainHandler 실행
     * 시간의 경우 현재 시간보다 이전시간이면 다음날로 예약함
     * ex) 현재시간 2021.06.01 16:00 인데 13시로 데이터가 들어오면 2021.06.02 13:00 으로 알람매니저 예약
     * UPDATE의 경우는 특정 시간마다 하루 1회 반복으로 돌아야함
     * 주기의 경우 요청한 시간 1회 요청 후 handler 실행
     * $ ./adb shell dumpsys alarm -> 등록된 알람 리스트를 확인할수 있음.
     * $ ./adb shell dumpsys alarm | grep 패키지명 -> 패키지명으로 등록된 알람 리스트를 확인할 수 있음.
     */

    private fun setAlarmTime(context: Context, requestType: Int, inputCal: Calendar) {
        context?.let {
            AppLog.d(TAG, "setAlarmTime requestType : ${requestType}")
            val alarmMgr = it.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            // Set the alarm to start at approximately 2:00 p.m.


            val currentTimeMillis = System.currentTimeMillis()
            var currentCal: Calendar = Calendar.getInstance().apply {
                timeInMillis = currentTimeMillis
            }

            AppLog.d(TAG, "currentHour : ${currentCal.get(Calendar.HOUR_OF_DAY)}")
            AppLog.d(TAG, "currentMinute : ${currentCal.get(Calendar.MINUTE)}")

            if (Build.VERSION.SDK_INT >= 23) {
                when (requestType) {
                    Constant.ALARM_TYPE_CHECK_IS_UPDATE -> {

                        var pendingIntent = PendingIntent.getBroadcast(
                            context, requestType,
                            Intent(context, AlarmBroadCastReceiver::class.java).apply {
                                putExtra(Constant.KEY.TYPE, requestType)
                                action = Constant.INTENT_ACION.ACTION_ALARM_HEALTH
                            }, PendingIntent.FLAG_UPDATE_CURRENT
                        )

                        alarmMgr?.apply {
                            val calendar: Calendar = Calendar.getInstance().apply {
                                timeInMillis = currentTimeMillis
                                set(Calendar.HOUR_OF_DAY, inputCal.get(Calendar.HOUR_OF_DAY))
                                set(Calendar.MINUTE, inputCal.get(Calendar.MINUTE))
                                set(Calendar.SECOND, 0)
                                add(Calendar.DATE, 1)
                            }
                            AppLog.d(TAG, "calendar : {${calendar.timeInMillis}}")
                            cancel(pendingIntent)
                            setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                calendar.timeInMillis,
                                pendingIntent
                            )
                        }
                    }
                    Constant.ALARM_TYPE_CHECK_REMAIN -> {

                        var pendingIntent = PendingIntent.getBroadcast(
                            context, requestType,
                            Intent(context, RemainBroadCastReceiver::class.java).apply {
                                putExtra(Constant.KEY.TYPE, requestType)
                                action = Constant.INTENT_ACION.ACTION_ALARM_HEALTH_REMAIN
                            }, PendingIntent.FLAG_UPDATE_CURRENT
                        )

                        alarmMgr?.apply {
                            val calendar: Calendar = Calendar.getInstance().apply {
                                timeInMillis = currentTimeMillis
                                set(Calendar.HOUR_OF_DAY, inputCal.get(Calendar.HOUR_OF_DAY))
                                set(Calendar.MINUTE, inputCal.get(Calendar.MINUTE))
                                set(Calendar.SECOND, 0)
                                AppLog.d("cal : ${inputCal.get(Calendar.HOUR_OF_DAY)}, ${inputCal.get(Calendar.MINUTE)},current :${currentCal.get(Calendar.HOUR_OF_DAY)},${currentCal.get(Calendar.MINUTE)}")

                                /**
                                 * 날짜를 다음날로 이관하는 경우
                                 * 1. 서버로 받은 Hour이 현재 Hour보다 큰 경우 ->(ex : input 15 current 13)
                                 * 2. 시간은 같으나 서버에서 받은 minute가 더 큰 경우(ex : input 30 current 20)
                                 */

                                //시간이 큰 경우
                                if (inputCal.get(Calendar.HOUR_OF_DAY) < currentCal.get(Calendar.HOUR_OF_DAY)) {
                                    add(Calendar.DATE,1)
                                }
                                //시간이 같은 경우 서버의 분이 크면 다음날로 전달
                                else if (inputCal.get(Calendar.HOUR_OF_DAY) == currentCal.get(Calendar.HOUR_OF_DAY)) {
                                    AppLog.d(TAG,"inputCal : ${inputCal.get(Calendar.MINUTE)}, currentCal : ${currentCal.get(Calendar.MINUTE)}")
                                    if (inputCal.get(Calendar.MINUTE) <= currentCal.get(Calendar.MINUTE)) {
                                        add(Calendar.DATE, 1)
                                    }
                                }
                            }
                            cancel(pendingIntent)
                            AppLog.d(TAG, "calendar2 : {${calendar.timeInMillis}}")
                            setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                calendar.timeInMillis,
                                pendingIntent
                            )
                        }
                    }
                }
            }
        }
    }

    fun getAlarmDate(date: String?, pattern: String = "HH:mm"): Calendar {

        date?.let {
            try {
                return Calendar.getInstance().apply {
                    time = SimpleDateFormat(pattern).parse(date)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return getDefaultCalendar()
            }
        }
        return getDefaultCalendar()
    }

    // 현재 시간으로 등록시 다음날 시간으로 알람이 등록됨
    // 00:00이 기본 시간
    private fun getDefaultCalendar(isDebug: Boolean = false): Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
//        return getDebugDefaultCalendar()
    }

    private fun getDebugDefaultCalendar(): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, 2021)
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 43)
            set(Calendar.SECOND, 0)
        }
    }

    private fun setUpdateError() {
        AppLog.l()
        errCnt++
        if (errCnt >= Constant.ERROR_UPDATE_CNT) {
            clearUpdateCheckHandler()
            cancelRemainAlarm()
        } else {
            updateCheckHandler.removeCallbacks(updateRunnable)
            updateCheckHandler.postDelayed(updateRunnable, Constant.UPDATE_CHECK_TIME)
        }
    }

    private fun clearUpdateCheckHandler(){
        updateCheckHandler.removeCallbacks(updateRunnable)
        errCnt = 0
    }

    /**
     * 정기 업데이트 알람 삭제
     */
    private fun cancelRemainAlarm(){
        AppLog.l()
        val alarmMgr = applicationContext.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        var pendingIntent = PendingIntent.getBroadcast(
            applicationContext, Constant.ALARM_TYPE_CHECK_IS_UPDATE,
            Intent(applicationContext, AlarmBroadCastReceiver::class.java).apply {
                putExtra(Constant.KEY.TYPE, Constant.ALARM_TYPE_CHECK_IS_UPDATE)
                action = Constant.INTENT_ACION.ACTION_ALARM_HEALTH
            }, PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmMgr?.cancel(pendingIntent)
    }

    /**
     * update가 있는 경우 업데이트 삭제
     */
    private fun cancelUpdateAlarm(){
        AppLog.l()
        val alarmMgr = applicationContext.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        var pendingIntent = PendingIntent.getBroadcast(
            applicationContext, Constant.ALARM_TYPE_CHECK_REMAIN,
            Intent(applicationContext, RemainBroadCastReceiver::class.java).apply {
                putExtra(Constant.KEY.TYPE, Constant.ALARM_TYPE_CHECK_REMAIN)
                action = Constant.INTENT_ACION.ACTION_ALARM_HEALTH_REMAIN
            }, PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmMgr?.cancel(pendingIntent)
    }

    /**
     * 알람 전부 삭제
     */
    private fun cancelAllAlarm(){
        AppLog.l()
        cancelUpdateAlarm()
        cancelRemainAlarm()
    }

    override fun onDestroy() {
        updateCheckHandler.removeCallbacksAndMessages(null)
        alarmReceiveReceiver?.let {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(it)
        }
        cancelAllAlarm()
        super.onDestroy()
    }

    companion object {
        val TAG: String = HealthCheckService::class.java.simpleName
    }
}