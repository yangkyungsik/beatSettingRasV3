package com.beat.settingras

object Constant {

    const val IS_LOG = true
    const val SERVER_PORT = 5000

    const val LOG_FOLDER ="beatlog"
    const val LOG_FILE = "logfile_"
    const val LOG_FILE_PREFIX = ".txt"

    const val DEVICE_TYPE_L = "SIGNAGE3_LEFT"
    const val DEVICE_TYPE_R = "SIGNAGE3_RIGHT"

    const val IP_L = "192.168.190.203"
    const val IP_R = "192.168.190.204"

    const val SERVER_RETRY_MAX_CNT:Int = 3
    const val DATE_YYYYMMDDHHMMSS = "yyyyMMDDHHMMSS"

    const val ALARM_TYPE_CHECK_IS_UPDATE = 0 // 주기 시간에 맞춰 업데이트 여부 확인 특정 시간
    const val ALARM_TYPE_CHECK_REMAIN = 1 // 업데이트 API 준 시간에 맞춰서 잔존 여부 확인

    val HEALTH_CHECK_TIME:Long = if(BuildConfig.IS_REAL) 1000 * 60 * 60 else 1000 * 60 // 개발 1시간 실제 1시간
    val UPDATE_CHECK_TIME:Long = if(BuildConfig.IS_REAL) 1000 * 60 * 20 else 1000 * 10 // 개발 20분 실제 1분
    val REMAIN_WEB_RESTORE_TIME:Long = if(BuildConfig.IS_REAL) 1000 * 30 else 1000 * 10 // 웹뷰 복구 시간 30초
    val ERROR_UPDATE_CNT:Int = 3

    object KEY{
        const val TYPE = "type"
        const val TITLE = "title"
        const val URL = "url"
        const val MSG = "message"
        const val DATA = "data"
        const val PLAYBACK = "playback"
        const val IP = "ip"
        const val PORT = "port"
        const val USERNAME = "username"
        const val PW = "password"
        const val STORECODE = "storecode"
    }

    object VALUE {
        const val CONFIRM = "confirm"
        const val ALERT = "alert"
        const val LOCAL = "local"
        const val DOWNLOAD = "download"
    }

    object ACTION{
        const val PLAYBACK = "com.beat.display.playback"
    }

    object KOINNAME{
        const val COMMON_NETWORK="COMMON"
        const val BOOTH_NETWORK="BOOTH_NETWORK"
        const val SSL="SSL"
    }

    object INTENT_ACION{
        const val REFRESH_WEBVIEW_SCREEN= "refresh_webview_screen"
        const val ACTION_ALARM_HEALTH= "com.beat.signage.action_health"
        const val ACTION_ALARM_HEALTH_REMAIN= "com.beat.signage.action_health_remain"
        const val ACTION_ALARM_HEALTH_RECEIVE= "com.beat.signage.action_health_receive"
    }

    object PARAM{
        const val DEVICE_TYPE = "device_type"
        const val VERSION_CODE = "version_code"
        const val BOOTH_CODE = "booth_code"
        const val SECURE_ANDROID_ID = "secure_android_id"
    }

    object REQCODE{
        const val PERMISSION_EXTERNAL_STORAGE = 10000
    }

    object FILENAME{
        const val IDLE_0001 = "idle_0001"
        const val DEFAULT2 = "beat_emotion_default_ver2"
        const val DETECT_0001 = "detect_0001"
        const val DETECT_0002 = "detect_0002"
        const val DETECT_0003 = "detect_0003"
        const val MAINTAIN_0001 = "maintain_0001"
        const val ORDER_0001 = "order_0001"
        const val SERVING_0001 = "serving_0001"
        const val SERVING_0002 = "serving_0002"
        const val WORKING_0001 = "working_0001"
        const val CLOSED_0001 = "closed_0001"
        const val STORE_CODE:String = "storecode.json"
        const val IP_CODE:String = "ipcode.json"
    }

}