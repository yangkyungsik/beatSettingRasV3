package com.beat.settingras.data.remote.source.api

import com.beat.settingras.data.remote.source.API
import com.beat.settingras.data.remote.source.response.common.CenterVersionInfoItem
import com.beat.settingras.data.remote.source.response.common.SysInfoItem
import com.beat.settingras.data.remote.source.response.common.VersionCheckItem
import com.beat.settingras.data.remote.source.response.common.VersionStatusItem
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST


/**
 * 공통 @Header값은 OkHttp 라이브러리의 addIntercepter()를 선언하여 사전 구현이 가능.
 *
 * @QueryMap : @GET 방식 파라메터를 전송할 경우
 * @FieldMap : @POST 방식 파라메터를 전송할 경우
 * @Body : Content-Type이 application/json으로 전송할 경우
 *
 * @FormUrlEncoded 어노테이션은
 * @Field 어노테이션을 인자값으로 한개 이상 사용했을경우에만 가능하다.
 *
 * @Header 어노테이션을 인자값으로 받으면 해당 인자값이 동적으로 @Headers에 추가.
 * 모든 Request의 공통 헤더값은 OkHttpClient.Builder()시 addInterceptor()를 사용하여 사전 정의.
 *
 */

interface CommonAPI {
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("beat_app/api/v2/sysinfo")
    fun requestSysInfo(@HeaderMap params:MutableMap<String,Any>) : Single<SysInfoItem>


    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("beat_app/api/v1/eye/check/version")
    fun requestVersionInfo(@HeaderMap header:MutableMap<String,Any?>,@Body params:MutableMap<String,Any?>): Single<CenterVersionInfoItem>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST(API.URI.BEAT_APP+ API.URI.DEVICE+API.URI.VERSION_CHECK)
    fun requestVersionCheck(@HeaderMap header:MutableMap<String,Any?>,@Body params:MutableMap<String,Any?>): Single<VersionCheckItem>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST(API.URI.BEAT_APP+API.URI.DEVICE+API.URI.VERSION_STATUS)
    fun requestVersionStatus(@HeaderMap header:MutableMap<String,Any?>,@Body params:MutableMap<String,Any?>): Single<VersionStatusItem>

}