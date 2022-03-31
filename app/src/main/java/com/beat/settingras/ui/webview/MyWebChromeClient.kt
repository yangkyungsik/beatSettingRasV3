package com.beat.settingras.ui.webview

import android.os.Message
import android.webkit.JsResult
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.beat.settingras.AppLog
import com.beat.settingras.Constant
import com.beat.settingras.ui.model.WebViewModel

class MyWebChromeClient : WebChromeClient {

    lateinit var webViewModel: WebViewModel
    constructor(webViewModel: WebViewModel){
        this.webViewModel = webViewModel
    }

    override fun onCreateWindow(
        view: WebView?,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?
    ): Boolean {
        var params:HashMap<String,Any?> = HashMap<String,Any?>()
        params[Constant.KEY.TYPE] = "onCreateWindow"
        params[Constant.KEY.DATA] = resultMsg
        webViewModel.dataInit.value = params
        return true;
    }


    override fun onPermissionRequest(request: PermissionRequest?) {
        super.onPermissionRequest(request)
        AppLog.d("DALKOMM", "onPermissionRequest")

    }

    override fun onPermissionRequestCanceled(request: PermissionRequest?) {
        super.onPermissionRequestCanceled(request)
    }

    override fun onJsAlert(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        var hashMap:HashMap<String,Any?> = HashMap<String,Any?>()
        hashMap[Constant.KEY.TYPE] = Constant.VALUE.ALERT
        hashMap[Constant.KEY.MSG] = message
        hashMap[Constant.KEY.DATA] = result
        webViewModel.jsAlert.value = hashMap
        return true
    }

    override fun onJsConfirm(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        var hashMap:HashMap<String,Any?> = HashMap<String,Any?>()
        hashMap[Constant.KEY.TYPE] = Constant.VALUE.CONFIRM
        hashMap[Constant.KEY.MSG] = message
        hashMap[Constant.KEY.DATA] = result
        webViewModel.jsAlert.value = hashMap
        return true
    }


}