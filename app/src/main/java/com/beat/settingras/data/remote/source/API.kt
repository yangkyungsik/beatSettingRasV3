package com.beat.settingras.data.remote.source

import com.beat.settingras.BuildConfig

open class API {
    val DATE_YYYYMMDDHHMMSS = "yyyyMMDDHHMMSS"
    val CENTER_API_AUTH_USERNAME = "beat_eye" // CENTER USERNAME
    val CENTER_APP_ID = "beat_eye_api"
    val CENTER_API_AUTH_KEY = "2020_beat_eye"

    object URL{
        const val BOOTH_URL = "http://192.168.190.82:8888" //기본IP

        const val URL_DEV = "https://dev-app.beatcorp.io/beat_app/" // API (개발서버) IDC
        const val URL_REAL_URL = "https://app.beatcorp.io/beat_app/" // API (실서버)

        const val WEB_DEV_URL = "https://dev-signage.beatcorp.io/3.0/"
        const val WEB_REAL_URL = "https://signage.beatcorp.io/3.0/"

        const val WEB_ERROR_LOCAL_URL = "file:///android_asset/error.html"

        val BASE_URL: String = if (BuildConfig.IS_REAL) URL_REAL_URL else URL_DEV
        val BASE_WEB_URL: String = if (BuildConfig.IS_REAL) WEB_REAL_URL else WEB_DEV_URL

        val CENTER_API_AUTH_USERNAME = "beat_eye" // CENTER USERNAME
        val CENTER_APP_ID = "beat_eye_api"
        val CENTER_API_AUTH_KEY = "2020_beat_eye"
    }

    object Header {
        const val CONTENT_TYPE = "Content-Type" //기본
        const val APP_LANG = "X-beat-app-lang" //인증 요청시 기본 "ko"
        const val APP_ID = "X-beat-app-id" //기본
        const val APP_TYPE = "X-beat-app-type" //기본
        const val APP_VERSION = "X-beat-app-version" //기본
        const val ACCESS_TOKEN = "X-beat-access-token" //인증 후
        const val AUTHORIZATION = "Authorization" //인증 요청시
        const val HEADER_UDID = "X-beat-store" //udid 페이크키

        object ContentType {
            const val CONTENT_TYPE_JSON = "application/json;charset=utf-8" //json
            const val CONTENT_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded" //xwwwform
            const val CONTENT_TYPE_TEXT_HTML = "text/html" //html
            const val CONTENT_TYPE_TEXT_PLAIN = "text/plain" //text/plain
        }
    }

    object RETURN {
        const val SUCCESS = "20000"
        const val IS_EXIST_USER = "20010"
        const val NOT_EXIST_USER = "20011"
        const val RESULT_NOT_AVAILABLE = "009"
        const val NOT_VERIFIED_PASSWORD = "20023" //비밀번호 불일치
        const val ALREADY_LEAVE_USER = "20033" //이미 탈퇴한 유저
        const val FORCE_LOGOUT = "20035" //중복 로그인 유저
        const val INVALID_AUTH = "20088"
        const val INVALID_DEVICE_ID = "20093"
        const val INVALID_PARAM = "40000"
        const val INVALID_AUTHORIZATION = "40100"
        const val INVALID_TOKEN = "40102"
        const val MISSING_HEADER = "40103"
        const val INVALID_METHOD = "40300"
        const val NOT_FOUND = "40400"
        const val INTERNAL_ERROR = "50000"
        const val INVALID_MOBILE_NUMBER = "200500" //유효하지 않은 휴대폰 번호입니다.
        const val USER_NOT_EXISTS = "20011" //사용자 정보가 없습니다.
        const val INtERGRATE_MIGRATION_FAIL = "20038" //통합전환이 실패하였습니다.
        const val EXPIRED_MOBILE_CERT = "200501" //입력시간이 만료되었습니다. 재발송을 해주세요.
        const val INVALID_MOBILE_CERT_CODE = "200502" //인증번호가 일치하지 않습니다.
        const val SMS_SEND_FAILED = "200503" // 인증번호 발송을 실패하였습니다.
        const val MOBILE_CERT_NOT_EXIST = "200504" // 인증번호 발급을 실행해주세요.
        const val USER_ALREADY_EXIST = "200506" //이미 가입된 휴대폰 번호입니다.
        const val LOGIN_EMAIL_IS_DUPLICATE = "200507" // 이미 등록된 이메일 주소입니다.
        const val EMAIL_IS_INVALID = "200508" // 올바른 이메일 형태가 아닙니다.
        const val IS_ALREADY_EMAIL_USER = "200509" // 이미 이메일 계정으로 전환하였습니다.
        const val INVALID_AGE = "200510" //만 14세 이상만 이용 가능합니다.
        const val FIND_PASSWORD_NOT_EXIST_USER = "200511" //만 14세 이상만 이용 가능합니다.
        const val UPDATE_PROFILE = "200512" //업데이트 중 에러
        const val IS_SLEEP_USER = "200519" //로그인 중 휴면회원확인
    }

    object URI{
        const val BEAT_APP = "/beat_app/api"
        const val DEVICE = "/device"
        const val VERSION_CHECK = "/version/check"
        const val VERSION_STATUS = "/version/status"
    }

    object BOOTHRETURN{
        const val SUCCESS_CODE = "0000"
        const val SUCCESS_MSG = "SUCCESS"
    }
}