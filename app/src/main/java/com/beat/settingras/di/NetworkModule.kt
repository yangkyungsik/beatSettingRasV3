package com.beat.settingras.di

import com.beat.settingras.Constant
import com.beat.settingras.data.remote.source.API
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val CONNECT_TIMEOUT = 5L
private const val WRITE_TIMEOUT = 5L
private const val READ_TIMEOUT = 5L

val networkModule = module{

    single { Cache(androidApplication().cacheDir,10L*1024*1024) }
    single { GsonBuilder().create() }
    single {
        OkHttpClient.Builder().apply {
            cache(get())
            connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            retryOnConnectionFailure(true)
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }.build()
    }

    single(named(Constant.KOINNAME.COMMON_NETWORK)) {
        Retrofit.Builder()
            .baseUrl(API.URL.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(get()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(get())
            .build()
    }

    single(named(Constant.KOINNAME.BOOTH_NETWORK)) {
        Retrofit.Builder()
            .baseUrl(API.URL.BOOTH_URL)
            .addConverterFactory(GsonConverterFactory.create(get()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(get())
            .build()
    }
}
