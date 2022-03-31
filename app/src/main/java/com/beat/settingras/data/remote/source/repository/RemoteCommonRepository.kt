package com.beat.settingras.data.remote.source.repository

import android.util.Base64
import com.beat.settingras.AppLog
import com.beat.settingras.Constant
import com.beat.settingras.data.remote.source.API
import com.beat.settingras.data.remote.source.base.BaseCommonRepository
import com.beat.settingras.data.remote.source.response.common.CenterVersionInfoItem
import com.beat.settingras.util.CommonUtil
import com.beat.settingras.data.remote.source.api.CommonAPI
import com.beat.settingras.data.remote.source.response.common.VersionCheckItem
import com.beat.settingras.data.remote.source.response.common.VersionStatusItem
import io.reactivex.Single
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SignatureException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class RemoteCommonRepository(private val commonAPI: CommonAPI) : BaseCommonRepository {

    override fun getDefaultHeader(): MutableMap<String, Any?> = getDefaultHeaders()

    override fun getDefaultHeader(isOAuthRequest: Boolean): MutableMap<String, Any?> =
        getDefaultHeaders(true, isOAuthRequest, false)

    override fun requestVersionInfo(map: MutableMap<String, Any?>): Single<CenterVersionInfoItem> =
        commonAPI.requestVersionInfo(getDefaultHeader(), map)

    override fun requestVersionCheck(map: MutableMap<String, Any?>): Single<VersionCheckItem> =
        commonAPI.requestVersionCheck(getDefaultHeader(), map)

    override fun requestVersionStatus(map: MutableMap<String, Any?>): Single<VersionStatusItem> = commonAPI.requestVersionStatus(getDefaultHeader(),map)

    private fun getDefaultHeaders(): MutableMap<String, Any?> =
        getDefaultHeaders(isPost = true, isAuthentication = true, isAccessToken = false)

    private fun getDefaultHeaders(
        isPost: Boolean,
        isAuthentication: Boolean,
        isAccessToken: Boolean
    ): MutableMap<String, Any?> {
        val params: MutableMap<String, Any?> = HashMap<String, Any?>()

        if (isPost)
            params[API.Header.CONTENT_TYPE] = "application/json;charset=utf-8"

        if (isAuthentication)
            params[API.Header.AUTHORIZATION] = genAuthorization()

        return params
    }

    private fun genAuthorization(): String {
        var auth: String = ""
        val sdf = SimpleDateFormat(Constant.DATE_YYYYMMDDHHMMSS, Locale.getDefault())
        val timeStamp = sdf.format(Date(System.currentTimeMillis()))
        try {
            auth = "Basic " + Base64.encodeToString(
                (API.URL.CENTER_API_AUTH_USERNAME + ":" +
                        API.URL.CENTER_APP_ID + ";" + CommonUtil.genHMACSHA1(
                    API.URL.CENTER_APP_ID + timeStamp,
                    API.URL.CENTER_API_AUTH_KEY
                ) + ";" +
                        timeStamp).toByteArray(), Base64.NO_WRAP
            )
        } catch (e: SignatureException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        }
        AppLog.d("Authorization = $auth")

        return auth
    }
}