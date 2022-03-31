package com.beat.settingras

import android.util.Log
import com.beat.settingras.util.FileUtil
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 공통 로그 클래스.
 */
object AppLog {
    private const val LOGGABLE = true
    var REPORTABLE = false
    private const val TAG = "BEATFACE"
    private const val FORMAT_LOG = "[%s] %s"
    private const val FORMAT_REPORT = "[%s] [%s] %s"
    private val FORMAT_DATE =
        SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
    private const val CATEGORY_VERBOSE = "VERBOSE"
    private const val CATEGORY_API = "API"
    private const val CATEGORY_EVENT = "EVENT"
    private const val CATEGORY_INFO = "INFO"
    private const val CATEGORY_ERROR = "ERROR"

    fun v(category: String?, msg: String?) {
        val e = Exception()
        val element = e.stackTrace

        var data:String? = null
        if(LOGGABLE)
            data = "(" + element[1].fileName + ":" + element[1].lineNumber + ")" + element[1].methodName+" -> " + msg
        else {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            data = "[$date]"+"(" + element[1].fileName + ":" + element[1].lineNumber + ")" + element[1].methodName+" -> " + msg
        }


        if (LOGGABLE) {
            Log.v(category, data)
//            fileLog(data)
        }
    }

    fun d(category: String?, msg: String?) {
        val e = Exception()
        val element = e.stackTrace
        var data:String? = null
        if(LOGGABLE)
            data = "(" + element[1].fileName + ":" + element[1].lineNumber + ")" + element[1].methodName+" -> " + msg
        else {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            data = "[$date]"+"(" + element[1].fileName + ":" + element[1].lineNumber + ")" + element[1].methodName+" -> " + msg
        }

        if (LOGGABLE) {
  //          fileLog(data)
            Log.d(category, data)
        }
    }

    fun i(category: String?, msg: String?) {
        val e = Exception()
        val element = e.stackTrace
        var data:String? = null
        if(LOGGABLE)
            data = "(" + element[1].fileName + ":" + element[1].lineNumber + ")" + element[1].methodName+" -> " + msg
        else {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            data = "[$date]"+"(" + element[1].fileName + ":" + element[1].lineNumber + ")" + element[1].methodName+" -> " + msg
        }


        if (LOGGABLE) {
//            fileLog(data)
            Log.i(category, data)
        }
    }

    fun e(category: String?, msg: String?) {
        val e = Exception()
        val element = e.stackTrace

        var data:String? = null
        if(LOGGABLE)
            data = "(" + element[1].fileName + ":" + element[1].lineNumber + ")" + element[1].methodName+" -> " + msg
        else {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            data = "[$date]"+"(" + element[1].fileName + ":" + element[1].lineNumber + ")" + element[1].methodName+" -> " + msg
        }

//        fileLog(data)
        if (LOGGABLE)
            Log.e(category, data)
    }


    fun w(category: String?, msg: String?) {
        val e = Exception()
        val element = e.stackTrace
        var data:String? = null
        if(LOGGABLE)
            data = "(" + element[1].fileName + ":" + element[1].lineNumber + ")" + element[1].methodName+" -> " + msg
        else {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            data = "[$date]"+"(" + element[1].fileName + ":" + element[1].lineNumber + ")" + element[1].methodName+" -> " + msg
        }


        if (LOGGABLE) {
            Log.w(category, data)
//            fileLog(data)
        }
    }


    //add phikim
    fun d(msg: String) {
        val e = Exception()
        val element = e.stackTrace
        var data:String? = null
        if(LOGGABLE)
            data = "(" + element[1].fileName + ":" + element[1].lineNumber + ")" + element[1].methodName+" -> " + msg
        else {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            data = "[$date]"+"(" + element[1].fileName + ":" + element[1].lineNumber + ")" + element[1].methodName+" -> " + msg
        }

//        fileLog(data)
        if (LOGGABLE)
            Log.d(TAG, data)
    }

    fun l() {
        val e = Exception()
        val element = e.stackTrace
        var msg:String?=null
        if(LOGGABLE)
            msg = "(" + element[1].fileName + ":" + element[1].lineNumber + ")" + element[1].methodName
        else {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            msg = "[$date]"+"(" + element[1].fileName + ":" + element[1].lineNumber + ")" + element[1].methodName
        }

//        fileLog(msg)
        if (LOGGABLE)
            Log.d(TAG, msg)

    }

    fun e(msg: String) {
        val e = Exception()
        val element = e.stackTrace

        var data:String? = null
        if(LOGGABLE)
            data = "(" + element[1].fileName + ":" + element[1].lineNumber + ")" + element[1].methodName+" -> " + msg
        else {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            data = "[$date]"+"(" + element[1].fileName + ":" + element[1].lineNumber + ")" + element[1].methodName+" -> " + msg
        }

//        fileLog(data)
        if (LOGGABLE)
            Log.e(TAG, data)
    }


    @JvmStatic
    private fun fileLog(msg: String?) {
        saveLog(msg)
    }


    fun saveE(category: String?, msg: String?) {
        if(REPORTABLE==false)
            return
        try{
            val e = Exception()
            val element = e.stackTrace

            var data:String? = null
            if(LOGGABLE)
                data = "(" + element[1].fileName + ":" + element[1].lineNumber + ")" + element[1].methodName+" -> " + msg
            else {
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                data = "[$date]"+"(" + element[1].fileName + ":" + element[1].lineNumber + ")" + element[1].methodName+" -> " + msg
            }

            fileLog(data)
            if (LOGGABLE)
                Log.e(category, data)
        }
        catch (e:Exception){
            e.printStackTrace()
        }

    }




    @JvmStatic
    fun saveLog(text:String?){
        try{
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            var file = File(FileUtil.getLogFolderPath()+Constant.LOG_FILE+date+Constant.LOG_FILE_PREFIX)
            var folder = File(FileUtil.getLogFolderPath())

            if(!folder.exists()){
                folder.mkdir()
                file.createNewFile()
            }

            if(!file.exists()){
                file.createNewFile()
            }

            val time = SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(Date())

            file.appendText("$time:$text\n",Charsets.UTF_8)
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }
}