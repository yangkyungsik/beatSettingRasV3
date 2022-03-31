package com.beat.settingras.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.beat.settingras.Constant
import com.beat.settingras.databinding.ActivityServerBinding
import com.beat.settingras.ui.BaseActivity
import com.beat.settingras.ui.ServerService
import com.beat.settingras.ui.listener.TransFilenameListener
import com.beat.settingras.ui.model.ServerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class ServerViewActivity : BaseActivity<ServerViewModel>(ServerViewModel::class),
    TransFilenameListener {

    private val serverViewModel: ServerViewModel by viewModel()
    val networkTimer = Timer()
    lateinit var binding:ActivityServerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setObserver() {
        serverViewModel.serverStatus.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                binding.serverTextView.text = it
            }
        })
    }

    override fun onResume() {
        super.onResume()

        startService(Intent(applicationContext, ServerService::class.java))
        transFileName("default")
//        finish()

        viewModel.setTransFileNameListener(this)

    }

    companion object {
        val TAG: String = ServerViewActivity::class.java.simpleName
    }

    override fun transFileName(fileName: String) {

        fileName?.let {
            if (it == "default") {
                var intent = Intent().apply {
                    setClass(applicationContext, VideoViewActivity::class.java)
                    putExtra(VideoViewActivity.TYPE, Constant.VALUE.LOCAL)
                    putExtra(VideoViewActivity.FILEPATH, it)
                    putExtra(VideoViewActivity.USE_CONTROLLER, false)
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                startActivity(intent)
                return
            }

            var intent = Intent().apply {
                setClass(applicationContext, VideoViewActivity::class.java)
                putExtra(VideoViewActivity.TYPE, Constant.VALUE.DOWNLOAD)
                putExtra(VideoViewActivity.FILEPATH, it)
                putExtra(VideoViewActivity.USE_CONTROLLER, false)
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        networkTimer.cancel()
        serverViewModel.stopServer()
        super.onDestroy()
    }
}