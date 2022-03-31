package com.beat.settingras.ui.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.webkit.JsResult
import android.webkit.WebView
import android.webkit.WebView.WebViewTransport
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.beat.settingras.AppLog
import com.beat.settingras.BuildConfig
import com.beat.settingras.Constant
import com.beat.settingras.data.remote.source.API
import com.beat.settingras.databinding.ActivityCommonWebviewBinding
import com.beat.settingras.ui.BaseActivity
import com.beat.settingras.ui.HealthCheckService
import com.beat.settingras.ui.model.WebViewModel
import com.beat.settingras.util.CommonUtil
import com.beat.settingras.util.FileUtil
import com.beat.settingras.util.SysUtils

class CommonWebViewActivity : BaseActivity<WebViewModel>(WebViewModel::class) {


    inner class WebViewRefreshReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            binding?.webview.apply {
                AppLog.d(TAG, "WebViewRefreshReceiver")
                clearCache(true)
                clearHistory()
                reload()
            }
        }
    }

    lateinit var binding:ActivityCommonWebviewBinding
    var mRefreshReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCommonWebviewBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if (CommonUtil.checkFilePermission(this)) {
            startWebProcess()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                Constant.REQCODE.PERMISSION_EXTERNAL_STORAGE
            )
        }
    }

    private fun startWebProcess() {
        setObserver()
        initWebViewSetting()
        startService(Intent(applicationContext, HealthCheckService::class.java))
        initReceiver()
    }

    private fun setObserver() {
        viewModel.jsAlert.observe(this, Observer {
            if (!isFinishing) {
                val msg: String? = it[Constant.KEY.MSG] as String?
                val type: String = it[Constant.KEY.TYPE] as String
                if (type == Constant.VALUE.CONFIRM) {
                    AlertDialog.Builder(CommonWebViewActivity@ this).setTitle("확인")
                        .setMessage(msg).setPositiveButton(android.R.string.ok) { dialog, which ->
                            run {
                                val jsResult = it[Constant.KEY.DATA] as JsResult
                                jsResult.confirm()
                            }
                        }
                        .setNegativeButton(android.R.string.cancel) { dialog, which ->
                            run {
                                val jsResult = it[Constant.KEY.DATA] as JsResult
                                jsResult.cancel()
                            }
                        }.show()
                } else if (type == Constant.VALUE.ALERT) {
                    AlertDialog.Builder(CommonWebViewActivity@ this).setTitle("확인")
                        .setMessage(msg).setPositiveButton(android.R.string.ok) { dialog, which ->
                            run {
                                val jsResult = it[Constant.KEY.DATA] as JsResult
                                jsResult.confirm()
                            }
                        }.setCancelable(false).create().show()
                }
            }
        })

        viewModel.dataInit.observe(this, Observer {
            val type = it[Constant.KEY.TYPE].toString()
            if (type == "onCreateWindow") {
                val resultMsg = it[Constant.KEY.DATA] as Message
                val newWebView = WebView(CommonWebViewActivity@ this)
                val transport = resultMsg.obj as WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()
            }
        })

        viewModel.orderIntent.observe(this, Observer {
            startActivity(it)
        })

        viewModel.errorUrl.observe(this, Observer {
            if (it == "")
                return@Observer

            AppLog.d(TAG, "observe errorUrl $it")
            binding?.webview?.apply {
                clearHistory()
                loadUrl(it)
            }
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebViewSetting() {

        // set web view
        binding?.webview.apply {
            viewModel.setWebViewSetting(this)
            viewModel.setDefaultJavascriptInterface(this)
            viewModel.setDefaultChromeWebViewClient(this)
        }

        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        binding.webview.requestFocus()
        binding.webview.loadUrl(API.URL.BASE_WEB_URL)

        binding.hidden1.setOnClickListener {
            SysUtils.reboot()
        }
        binding.hidden2.setOnClickListener {
            startSetting(this, findSettingPackages(this))
        }
    }

    private fun initReceiver() {
        mRefreshReceiver = WebViewRefreshReceiver().apply {
            val filter = IntentFilter()
            filter.addAction(Constant.INTENT_ACION.ACTION_ALARM_HEALTH_RECEIVE)
            LocalBroadcastManager.getInstance(applicationContext).registerReceiver(this, filter)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        FileUtil.writeIP()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(applicationContext, HealthCheckService::class.java))
        mRefreshReceiver?.let {
            LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(it)
        }
    }
}