package com.beat.settingras

import android.content.Context
import com.beat.settingras.util.SysUtils
import java.io.PrintWriter
import java.io.StringWriter

class ANRHandler
//    private Map<String, String> infos = new HashMap<String, String>();
//    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());

private constructor() : Thread.UncaughtExceptionHandler {
    private lateinit var mContext: Context

    /**
     * Initializes handler.
     */
    fun init(context: Context) {
        mContext = context
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    /**
     * Handles UncaughtException.
     */
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        if (handleException(ex)) {
            try {
                MainApplication.instance?.exit(true) //앱 강제 종료 및 재시작
                SysUtils.reboot()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * handle exception.
     */
    private fun handleException(ex: Throwable?): Boolean {
        if (ex == null) {
            return false
        }

        val writer = StringWriter()
        ex.printStackTrace(PrintWriter(writer))
        val s = writer.toString()
        AppLog.e(s)

        return true
    }

    companion object {
        val instance = ANRHandler()
    }

}
