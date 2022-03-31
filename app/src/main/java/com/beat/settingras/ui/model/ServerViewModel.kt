package com.beat.settingras.ui.model

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import com.beat.settingras.AppLog
import com.beat.settingras.Constant
import com.beat.settingras.ui.BaseViewModel
import com.beat.settingras.ui.listener.TransFilenameListener
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


class ServerViewModel : BaseViewModel() {

    private var serverUp = false
    private var lastFileName:String =""
    private var transFileListener:TransFilenameListener?=null
    private var retryServerTryCnt:Int = 0

    val serverStatus = MutableLiveData<String>()
    var isEthernetConnected:Boolean = false
    private val fileListResponse:String = FileUtil.getVideoFileOutput()
    private val filePath:String = FileUtil.getVideoPath()
    private val fileList = FileUtil.getVideoFileList()

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
            serverStatus.value = "server_running"
            retryServerTryCnt = 0
        } catch (e: IOException) {
            e.printStackTrace()
            mHttpServer = null
            retryServer()
            serverStatus.value = "Server is down retry Server 5 second"
            return
        }
    }

    fun stopServer() {
        mHttpServer?.let {
            it.stop(0)
            mHttpServer = null
            serverStatus.value = "Server is down"
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

    fun setTransFileNameListener(transFilenameListener: TransFilenameListener){
        this.transFileListener = transFilenameListener
    }

    private fun transDefaultFile(){
        transFileListener?.let {
            it.transFileName("default")
        }
    }


    /**
     * Activity로 파일경로 전달
     */
    private fun requestVideoFile(fileName:String){
        AppLog.l()
        if (isExistFile(fileName)) {
            //fileData.postValue(currentFileName)
            AppLog.d(TAG,"isExistVideoFile true")
            transFileListener?.let {
                it.transFileName("${filePath}${fileName}")
            }
            lastFileName = fileName
        }
    }

    /**
     * 파일명 확인z`
     */
    private fun isExistFile(fileName:String):Boolean{
        for (obj in fileList) {
            if(obj == fileName){
                return true
            }
        }
        return false
    }

    /**
     * N회 실행 후 안되면 OS 재부팅
     */
    private fun retryServer(){
        if(retryServerTryCnt!=Constant.SERVER_RETRY_MAX_CNT){
            retryHandler.postDelayed(Runnable {
                startServer(Constant.SERVER_PORT)
            },5000)
            retryServerTryCnt++
        }
        else 
            SysUtils.reboot()
        
    }
    
    companion object{
        val TAG: String = ServerViewModel::class.java.simpleName
        private var mHttpServer: HttpServer? = null
    }
}