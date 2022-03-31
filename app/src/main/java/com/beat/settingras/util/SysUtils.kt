
package com.beat.settingras.util

import com.beat.settingras.AppLog
import com.beat.settingras.BuildConfig
import java.text.SimpleDateFormat
import java.util.*

object SysUtils {

    val TAG = "SysUtils"

    fun reboot() {
        AppLog.l()
        try {
            val proc = Runtime.getRuntime()
                .exec(arrayOf("su", "-c", "reboot"))
            proc.waitFor()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun tAdbTcpip() {
        val t = Thread {
            try {
                var proc = Runtime.getRuntime()
                    .exec(arrayOf("su", "-c", "setprop service.adb.tcp.port 5555"))
                proc.waitFor()

                proc = Runtime.getRuntime()
                    .exec(arrayOf("su", "-c", "stop adbd"))
                proc.waitFor()

                proc = Runtime.getRuntime()
                    .exec(arrayOf("su", "-c", "start  adbd"))
                proc.waitFor()
            } catch (e: Exception) {
                AppLog.e(TAG,e.message)
            }
        }
        t.start()
    }

    fun tShowSystemKey(paramBoolean: Boolean) {

        Thread(Runnable {
            if(paramBoolean){
                var process:Process? = null
                try{
                    var localProcess:Process? = Runtime.getRuntime().exec(arrayOf("su","-c","am startservice -n com.android.systemui/.SystemUIService"))
                    process = localProcess
                    localProcess?.waitFor()
                    return@Runnable
                }catch (e:Exception){
                    AppLog.d(TAG,e.message)
                    try {
                        process?.waitFor()
                        return@Runnable
                    }catch (e:Exception){
                        AppLog.d(TAG,   "Failed to kill task bar (2).")
                        return@Runnable
                    }
                }
            }


            val magic ="42"
            try{
                var proc2:Process = Runtime.getRuntime().exec(arrayOf("su", "-c", "service call activity $magic s16 com.android.systemui"))
                proc2.waitFor()
                return@Runnable
            }catch (e:Exception){
                e.printStackTrace()
                AppLog.d(TAG,e.message)
            }
        }).start()
    }

    fun settingBottomNavBar(paramBoolean:Boolean) {
        if(!BuildConfig.DEBUG) {
            Thread(Runnable {
                if (paramBoolean) {
                    var process: Process? = null
                    try {
//                    var localProcess:Process? = Runtime.getRuntime().exec(arrayOf("su","-c","settings put global policy_control \"immersive.full=*\""))
                        Runtime.getRuntime().exec(arrayOf("su", "-c", "settings put secure user_setup_complete 0"))

//                    process = localProcess
//                    localProcess?.waitFor()
                        return@Runnable
                    } catch (e: Exception) {
                        AppLog.d(TAG,e.message)
                        try {
                            process?.waitFor()
                            return@Runnable
                        } catch (e: Exception) {
                            AppLog.d("Failed to kill task bar (2).")
                            return@Runnable
                        }
                    }
                }


                val magic = "42"
                try {
//                var proc2:Process? = Runtime.getRuntime().exec(arrayOf("su","-c","settings put global policy_control \"immersive.full=*\" "))
                    Runtime.getRuntime().exec(arrayOf("su", "-c", "settings put secure user_setup_complete 1"))
//                proc2?.waitFor()
                    return@Runnable
                } catch (e: Exception) {
                    e.printStackTrace()
                    AppLog.d(TAG,e.message)
                }
            }).start()
        }
    }

    /**
     * 시스템 시간 설정
     * odroid C4의 경우 하드웨어 재부팅 시 시간이 풀리는 현상 발경
     * 중앙서버로부터 시간 받아와서 처리함
     * 기본 패턴 지정
     * 루팅필수, 권한 필요
     * ex) 2021-06-22 17:39:00.000000
     */
    fun setSystemDate(date:String?,pattern:String="yyyy-MM-dd'T'hh:mm:ss.SSSSSS"){
        AppLog.l()

        val simpleDate = SimpleDateFormat(pattern)
        val parseDate = simpleDate.parse(date)
        val calendar = Calendar.getInstance()
        calendar.time = parseDate

        AppLog.d(TAG,"year : ${calendar.get(Calendar.YEAR)}, " +
                "month : ${String.format("%02d",calendar.get(Calendar.MONTH)+1)}, " +
                "date : ${String.format("%02d",calendar.get(Calendar.DAY_OF_MONTH))}," +
                "hour : ${String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY))}," +
                "minute : ${String.format("%02d",calendar.get(Calendar.MINUTE))}," +
                "second : ${String.format("%02d",calendar.get(Calendar.SECOND))}," +
                "millisecond : ${String.format("%06d",calendar.get(Calendar.MILLISECOND))}")

        var script:String = "date "+ String.format("%02d",calendar.get(Calendar.MONTH)+1)+
                String.format("%02d",calendar.get(Calendar.DAY_OF_MONTH))+
                String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY))+
                String.format("%02d",calendar.get(Calendar.MINUTE))+
                String.format("%02d",calendar.get(Calendar.YEAR))+"."+
                String.format("%02d",calendar.get(Calendar.SECOND))

        AppLog.d(TAG,"script : $script")

        Runtime.getRuntime().exec(arrayOf("su", "-c", "$script"))

    }

}