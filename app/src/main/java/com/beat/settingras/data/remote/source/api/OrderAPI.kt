package com.beat.settingras.data.remote.source.api

import com.beat.settingras.data.remote.BaseItem
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST

interface OrderAPI {
    @GET("/test/test")
    fun getTest(@HeaderMap params: MutableMap<String, Any>): Single<BaseItem>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/test/test")
    fun postTest(@HeaderMap params: MutableMap<String, Any>): Single<BaseItem>
}