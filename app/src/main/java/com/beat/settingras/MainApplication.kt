package com.beat.settingras

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.multidex.MultiDexApplication
import com.beat.settingras.di.commonRepositoryModule
import com.beat.settingras.di.networkModule
import com.beat.settingras.di.viewModelModule
import com.beat.settingras.ui.view.ServerViewActivity
import com.beat.settingras.util.CommonUtil
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import kotlin.system.exitProcess

class MainApplication : MultiDexApplication() {

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@MainApplication)
            modules(listOf(networkModule, commonRepositoryModule, viewModelModule))
        }
        if(!BuildConfig.DEBUG) {
            val anrhandler = ANRHandler.instance
            anrhandler.init(applicationContext)
        }

    }

    fun getLoacle():String =CommonUtil.getLocale(this)

    /**
     * 앱 강제 종료 및 재시작(ANR)
     */
    fun exit(restart: Boolean) {
        run {
            AppLog.i(TAG,"---------- exit application ----------")
            if (restart) {
                AppLog.i(TAG,"---------- restart application ----------")
                val pIntent = PendingIntent
                    .getActivity(applicationContext, 0, Intent(applicationContext, ServerViewActivity::class.java), 0)
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pIntent)
            }
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(10)
        }
    }

    companion object{
        val TAG = MainApplication::class.java.simpleName
        lateinit var INSTANCE:MainApplication

        var instance: MainApplication? = null
            get() {
                if (INSTANCE == null) {
                    exitProcess(0)
                    return null
                }
                return INSTANCE
            }
    }

}