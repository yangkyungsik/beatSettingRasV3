package com.beat.settingras.data.remote.source.repository

import com.beat.settingras.data.remote.source.api.UserAPI
import com.beat.settingras.data.remote.source.base.BaseUserRepository
import com.beat.settingras.data.remote.source.response.common.SysInfoItem
import io.reactivex.Single
import kotlin.collections.HashMap

@Deprecated("test")
class RemoteUserRepository(private val userAPI: UserAPI) : AbstractBaseRepository(),
    BaseUserRepository {
    override fun getTest(params: MutableMap<String, Any>) {

    }

    override fun requestSysInfo(): Single<SysInfoItem> {
        return userAPI.requestSysInfo(getDefaultHeaders())
    }

    override fun getDefaultHeader(): MutableMap<String, Any?> {
        return getDefaultHeaders()
    }

    override fun getDefaultHeader(isOAuthRequest: Boolean): MutableMap<String, Any?> {
        return getDefaultHeaders()
    }

    fun getDefaultHeaders(): MutableMap<String, Any?> {
        return getDefaultHeaders(isPost = true, isAuthentication = true, isAccessToken = false)
    }

    //TODO 실제 값은 아님 -> 서버에 맞춰 변경 필요
    fun getDefaultHeaders(isPost: Boolean, isAuthentication: Boolean, isAccessToken: Boolean): MutableMap<String, Any?> {
        val params: MutableMap<String, Any?> = HashMap()
        if (isPost)
            params[Header.CONTENT_TYPE] = "application/json;charset=utf-8"

        params[Header.APP_LANG] = "ko" //현재 시스템 언어 셋팅값 설정으로 변경 필요.
        params[Header.APP_ID] = "BEAT"
        params[Header.APP_TYPE] = "A"
        params[Header.APP_VERSION] = "2.1.9"
        params[Header.HEADER_UDID] = "MDAwMDAwMDAtNzNmZC0xOTQwLWVmMDUtYWM0YTAwMDAwMDAw"
        if (isAuthentication)
            params[Header.AUTHORIZATION] = "Basic YmVhdDpiZWF0X2FwcDs5YTlmNDE0YjkyNjhmMjUxNWE5Y2NmOTJjNjJkY2ZlZGFiOWZhYTRhOzIwMjAwODIwMTc1MzA4"
        if (isAccessToken)
            params[Header.ACCESS_TOKEN] = ""

        return params
    }

}