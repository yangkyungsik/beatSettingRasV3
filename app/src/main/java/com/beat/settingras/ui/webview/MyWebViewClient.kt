package com.beat.settingras.ui.webview

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.webkit.*
import com.beat.settingras.AppLog
import com.beat.settingras.data.remote.source.API
import com.beat.settingras.ui.model.WebViewModel

class MyWebViewClient : WebViewClient {

    var webViewModel:WebViewModel
    var isReceivedError:Boolean = false

    constructor(webViewModel: WebViewModel){
        this.webViewModel = webViewModel
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        webViewModel.progressDialog.value = true
        super.onPageStarted(view, url, favicon)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        AppLog.d("onPageFinished url : $url")

        webViewModel.progressDialog.value = false
        url?.let {
            if(it.contains(API.URL.BASE_WEB_URL) && !isReceivedError)
                webViewModel.stopRestoreWebView()
        }
        isReceivedError = false

        super.onPageFinished(view, url)
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        handler?.proceed()
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError
    ) {
        onReceivedError(view, error.errorCode, error.description.toString(), request?.url.toString())
    }

    override fun onReceivedError(
        view: WebView?,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        AppLog.l()
        isReceivedError = true
        webViewModel.errorUrl.postValue(API.URL.WEB_ERROR_LOCAL_URL)
        webViewModel.startRestoreWebView()
    }

}