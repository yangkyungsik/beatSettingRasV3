package com.beat.settingras.ui.model

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.beat.settingras.AppLog
import com.beat.settingras.Constant
import com.beat.settingras.R
import com.beat.settingras.ui.BaseViewModel
import com.beat.settingras.util.SysUtils
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MediaSourceEventListener
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter


class VideoViewModel : BaseViewModel() {


    lateinit var type:String
    lateinit var filePath:String

    //var fileQueue: Queue<String> = LinkedList<String>()
    var useController:Boolean = true


    val playerState = MutableLiveData<Int>(-1)
    val restart = MutableLiveData<Boolean>(false)
    var player: SimpleExoPlayer? = null

    val loadControl = DefaultLoadControl.Builder()
        .setAllocator(DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
        .setBufferDurationsMs(
            5 * 60 * 1000, // this is it!
            10 * 60 * 1000,
            DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
            DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
        )
        .setTargetBufferBytes(DefaultLoadControl.DEFAULT_TARGET_BUFFER_BYTES)
        .setPrioritizeTimeOverSizeThresholds(DefaultLoadControl.DEFAULT_PRIORITIZE_TIME_OVER_SIZE_THRESHOLDS)
        .createDefaultLoadControl()


    private var analyticsListener: AnalyticsListener = object : AnalyticsListener{
        override fun onPlayerError(eventTime: AnalyticsListener.EventTime, error: ExoPlaybackException) {
            super.onPlayerError(eventTime, error)
            AppLog.d(TAG,"onPlayerError")
            val writer = StringWriter()
            error.printStackTrace(PrintWriter(writer))
            val s = writer.toString()
            AppLog.e(s)
            restart.value = true
        }

        override fun onLoadStarted(
            eventTime: AnalyticsListener.EventTime, loadEventInfo: MediaSourceEventListener.LoadEventInfo,
            mediaLoadData: MediaSourceEventListener.MediaLoadData) {
            super.onLoadStarted(eventTime, loadEventInfo, mediaLoadData)
        }

        override fun onLoadingChanged(eventTime: AnalyticsListener.EventTime, isLoading: Boolean) {
            super.onLoadingChanged(eventTime, isLoading)
        }

        override fun onLoadCompleted(eventTime: AnalyticsListener.EventTime, loadEventInfo: MediaSourceEventListener.LoadEventInfo, mediaLoadData: MediaSourceEventListener.MediaLoadData) {
            super.onLoadCompleted(eventTime, loadEventInfo, mediaLoadData)
        }

        // 로드 에러 시 해당 event 실행
        /**
         *
         */
        override fun onLoadError(eventTime: AnalyticsListener.EventTime, loadEventInfo: MediaSourceEventListener.LoadEventInfo, mediaLoadData: MediaSourceEventListener.MediaLoadData, error: IOException, wasCanceled: Boolean) {
            super.onLoadError(eventTime, loadEventInfo, mediaLoadData, error, wasCanceled)
            AppLog.d(TAG, "onLoadError ")
//            val writer = StringWriter()
//            error.printStackTrace(PrintWriter(writer))
//            val s = writer.toString()
//            AppLog.e(s)
//            player?.let {
//                if(it.isPlaying)
//                    it.stop()
//                it.release()
//                player = null
//            }
//            finish.value = true

            SysUtils.reboot()
        }

        override fun onMediaPeriodReleased(eventTime: AnalyticsListener.EventTime) {
            super.onMediaPeriodReleased(eventTime)
        }

        override fun onLoadCanceled(
            eventTime: AnalyticsListener.EventTime,
            loadEventInfo: MediaSourceEventListener.LoadEventInfo,
            mediaLoadData: MediaSourceEventListener.MediaLoadData
        ) {
            super.onLoadCanceled(eventTime, loadEventInfo, mediaLoadData)
            AppLog.d(TAG, "onLoadError 2")
        }

        override fun onPlayerStateChanged(eventTime: AnalyticsListener.EventTime, playWhenReady: Boolean, playbackState: Int) {

            super.onPlayerStateChanged(eventTime, playWhenReady, playbackState)

            when(playbackState){
                Player.STATE_READY -> {
                    AppLog.d(TAG,"onPlayerStateChanged : Player.STATE_READY")
                    playerState.value = playbackState
                }

                Player.STATE_BUFFERING -> {
                    AppLog.d(TAG,"onPlayerStateChanged : Player.STATE_BUFFERING")
                    playerState.value = playbackState
                }

                Player.STATE_IDLE -> {
                    AppLog.d(TAG,"onPlayerStateChanged : Player.STATE_IDLE")
                    playerState.value = playbackState
                }

                Player.STATE_ENDED -> {
                    AppLog.d(TAG,"onPlayerStateChanged : Player.STATE_ENDED")
                    playerState.value = playbackState
                }

                else -> {
                    AppLog.d(TAG,"onPlayerStateChanged : other -> $playbackState")
                }
            }
        }

        override fun onPositionDiscontinuity(eventTime: AnalyticsListener.EventTime, reason: Int) {
            super.onPositionDiscontinuity(eventTime, reason)
            AppLog.d(TAG,"onPositionDiscontinuity : other -> $reason")
            playerState.value = Player.STATE_ENDED

        }
    }

    fun initExoPlayer(context:Context):SimpleExoPlayer?{
        AppLog.l()
        if(player==null) {
            player = SimpleExoPlayer.Builder(context)
                .setLoadControl(loadControl)
                .setUseLazyPreparation(true)
                .build()
        }
        return player
    }


    fun preparePlayer(context:Context, resolver: ContentResolver){
        AppLog.l()

            val mediaUri = Uri.fromFile(File(filePath))

            val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "basemvvm"))
            lateinit var videoSource:MediaSource

            if(type==Constant.VALUE.LOCAL) {
                val rawDataSource = RawResourceDataSource(context)
                rawDataSource.open(DataSpec(RawResourceDataSource.buildRawResourceUri(R.raw.idle_0001)))
                videoSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(rawDataSource.uri)
            }
            else if(type == Constant.VALUE.DOWNLOAD){
                videoSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaUri)
            }

            val loopingSource = LoopingMediaSource(videoSource)

        player?.let {
                it.apply {
                    addAnalyticsListener(analyticsListener)
                    playWhenReady = true
                    repeatMode = Player.REPEAT_MODE_ALL
                    prepare(loopingSource)
                }
            }
    }

    fun releasePlayer(){
        AppLog.l()
        player?.let {
            if(it.playbackState == Player.STATE_ENDED){
                analyticsListener?.let {
                    player?.removeAnalyticsListener(analyticsListener)
                }
                it.seekToDefaultPosition()
                it.clearVideoSurface()
                it.stop(true)
                it.release()
            }
        }
    }

    companion object{
        val TAG: String = VideoViewModel::class.java.simpleName
    }
}