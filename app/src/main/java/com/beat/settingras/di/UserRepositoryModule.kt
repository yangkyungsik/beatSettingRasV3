package com.beat.settingras.di

import com.beat.settingras.data.remote.source.api.UserAPI
import com.beat.settingras.data.remote.source.repository.RemoteUserRepository
import com.beat.settingras.data.remote.source.repository.UserRepository
import org.koin.dsl.module
import retrofit2.Retrofit

private fun provideApiService(retrofit: Retrofit): UserAPI = retrofit.create(UserAPI::class.java)

val userRepositoryModule = module{
    single{ provideApiService(get()) }
    single{ RemoteUserRepository(get()) }
    single{ UserRepository(get()) }
}