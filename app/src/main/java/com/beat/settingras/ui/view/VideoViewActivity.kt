package com.beat.settingras.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.beat.settingras.AppLog
import com.beat.settingras.Constant
import com.beat.settingras.R
import com.beat.settingras.databinding.ActivityVideoBinding
import com.beat.settingras.ui.BaseActivity
import com.beat.settingras.ui.model.VideoViewModel
import com.beat.settingras.util.CommonUtil
import com.google.android.exoplayer2.Player
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoViewActivity : BaseActivity<VideoViewModel>(VideoViewModel::class) {

    private val model : VideoViewModel by viewModel()
    private lateinit var binding:ActivityVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppLog.d(TAG,"onCreate")
    }

    override fun onPause() {
        super.onPause()
        AppLog.d(TAG,"onPause")
        binding.playerview.onPause()
    }

    override fun onResume() {
        AppLog.d(TAG,"onResume")
        super.onResume()
        initializePlayer()
    }

    private fun initializePlayer(){
        model.type = CommonUtil.getIntentExtra(intent,TYPE,"")
        if(intent.hasExtra(FILEPATH)) {
            CommonUtil.getIntentExtra(intent,FILEPATH,"")
            model.filePath = CommonUtil.getIntentExtra(intent,FILEPATH,"")
        }

        model.useController = intent.getBooleanExtra(USE_CONTROLLER,true)
        binding.playerview.useController = true

        if(model.player?.isPlaying==true){
            model.releasePlayer()
        }

        if(model.player==null)
            binding.playerview.player = model.initExoPlayer(applicationContext)

        model.preparePlayer(applicationContext, contentResolver)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        model.releasePlayer()
    }

    override fun initListener() {

    }

    override fun initObserver() {
        model.playerState.observe(this, Observer() {
            when(it){
                Player.STATE_ENDED ->{
                    model.releasePlayer()
                    model.preparePlayer(applicationContext, contentResolver)
                }
            }
        })

        model.restart.observe(this, Observer(){
            if(it==true){
                var intent = Intent().apply {
                    setClass(applicationContext,VideoViewActivity::class.java)
                    putExtra(VideoViewActivity.TYPE, Constant.VALUE.LOCAL)
                    putExtra(VideoViewActivity.FILEPATH,it)
                    putExtra(VideoViewActivity.USE_CONTROLLER,false)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
            }
        })
    }

    companion object{
        val FILEPATH = "FILEPATH"
        val TYPE = "TYPE"
        val USE_CONTROLLER = "use_controller"
    }
}