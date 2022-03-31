package com.beat.settingras.ui

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import com.beat.settingras.AppLog
import com.beat.settingras.Constant
import com.beat.settingras.ui.view.VideoViewActivity
import com.beat.settingras.util.FileUtil
import com.beat.settingras.util.SysUtils
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import java.io.IOException
import java.io.InputStream
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.Executors

class ServerService : Service() {

    private var serverUp = false
    private var lastFileName:String =""
    private var retryServerTryCnt:Int = 0
    private var mHttpServer: HttpServer? = null

    val fileListResponse:String = FileUtil.getVideoFileOutput()
    val filePath:String = FileUtil.getVideoPath()

    // Handler for root endpoint
    private val rootHandler = HttpHandler { exchange ->
        run {
            // Get request method
            when (exchange!!.requestMethod) {
                "GET" -> { sendResponse(exchange, "B;eat Face App") }
            }
        }
    }

    private val videoHandler = HttpHandler { httpExchange ->
        run {
            when (httpExchange!!.requestMethod) {
                "GET" -> {

                    var path = httpExchange.requestURI.path.split("/")

                    var currentFileName: String = path[path.size - 1]

                    AppLog.d(TAG, "****************************")
                    AppLog.d(TAG, "requestURI.PATH : ${httpExchange.requestURI.path}")
                    AppLog.d(TAG, "currentFileName : $currentFileName")
                    AppLog.d(TAG, "****************************")


                    if(currentFileName == "videos"){
                        sendResponse(httpExchange, fileListResponse)
                    }
                    else if(currentFileName == "default"){
                        transDefaultFile()
                    }
                    else{
                        if (lastFileName != currentFileName) {
                            if(currentFileName.contains("mp4")){
                                requestVideoFile(currentFileName)
                            }
                            else{
                                requestVideoFile("$currentFileName.mp4")
                            }
                        }
                    }
                }
            }
        }
    }

    private val commonHandler = HttpHandler { httpExchange ->
        run {
            when (httpExchange!!.requestMethod) {
                "GET" -> {
                    var path = httpExchange.requestURI.path.split("/")
                    if(path[path.size-1]=="reboot") {
                        SysUtils.reboot()
                        return@run
                    }
                    sendResponse(httpExchange, "Reboot")
                }
            }
        }
    }

    private val retryHandler = object : Handler(){}

    private fun streamToString(inputStream: InputStream): String {
        val s = Scanner(inputStream).useDelimiter("\\A")
        return if (s.hasNext()) s.next() else ""
    }

    private fun sendResponse(httpExchange: HttpExchange, responseText: String) {
        httpExchange.sendResponseHeaders(200, responseText.length.toLong())
        val os = httpExchange.responseBody
        os.write(responseText.toByteArray())
        os.close()
    }

    private fun startServer(port: Int) {
        try {
            mHttpServer?.let {
                it.stop(0)
                mHttpServer = null;
            }

            mHttpServer = HttpServer.create(InetSocketAddress(port), 0)
            mHttpServer?.let {
                it.executor = Executors.newCachedThreadPool()

                it.createContext("/", rootHandler)
                it.createContext("/index", rootHandler)
                it.createContext("/videos", videoHandler)
                it.createContext("/common",commonHandler)
                it.start() //startServer server;
            }
            retryServerTryCnt = 0
        } catch (e: IOException) {
            e.printStackTrace()
            mHttpServer = null
            retryServer()
            return
        }
    }

    fun stopServer() {
        mHttpServer?.let {
            it.stop(0)
            mHttpServer = null
        }
    }

    fun serverUp(){
        serverUp = if (!serverUp) {
            startServer(Constant.SERVER_PORT)
            transDefaultFile()
            true
        } else {
            AppLog.d(TAG,"serverUp is $serverUp reboot OS")
            SysUtils.reboot()
            false
        }
    }


    private fun transDefaultFile(){
        var intent = Intent().apply {
            setClass(applicationContext,VideoViewActivity::class.java)
            putExtra(VideoViewActivity.TYPE,Constant.VALUE.LOCAL)
            putExtra(VideoViewActivity.FILEPATH,"default")
            putExtra(VideoViewActivity.USE_CONTROLLER,false)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        applicationContext.startActivity(intent)
    }


    /**
     * Activity로 파일경로 전달
     */
    private fun requestVideoFile(fileName:String){
        AppLog.l()

        if (FileUtil.isExistVideoFile(fileName)) {
            AppLog.d(TAG,"isExistVideoFile true")
            transFileName("${filePath}${fileName}")
            lastFileName = fileName
        }
        else{
            AppLog.d(TAG,"isExistVideoFile false")
        }
    }

    /**
     * N회 실행 후 안되면 OS 재부팅
     */
    private fun retryServer(){
        if(retryServerTryCnt!= Constant.SERVER_RETRY_MAX_CNT){
            retryHandler.postDelayed(Runnable {
                startServer(Constant.SERVER_PORT)
            },5000)
            retryServerTryCnt++
        }
        else
            SysUtils.reboot()

    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serverUp()
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    private fun transFileName(fileName: String) {

        fileName?.let {
            if(it == "default"){
                var intent = Intent().apply {
                    setClass(applicationContext, VideoViewActivity::class.java)
                    putExtra(VideoViewActivity.TYPE,Constant.VALUE.LOCAL)
                    putExtra(VideoViewActivity.FILEPATH,it)
                    putExtra(VideoViewActivity.USE_CONTROLLER,true)
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                applicationContext.startActivity(intent)
                return
            }

            var intent = Intent().apply {
                setClass(applicationContext, VideoViewActivity::class.java)
                putExtra(VideoViewActivity.TYPE,Constant.VALUE.DOWNLOAD)
                putExtra(VideoViewActivity.FILEPATH,it)
                putExtra(VideoViewActivity.USE_CONTROLLER,true)
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            applicationContext.startActivity(intent)
        }
    }


    companion object{
        val TAG: String = ServerService::class.java.simpleName

    }

}