package com.beat.settingras.di

import com.beat.settingras.AppLog
import com.beat.settingras.Constant
import com.beat.settingras.data.remote.source.api.BoothAPI
import com.beat.settingras.data.remote.source.api.CommonAPI
import com.beat.settingras.data.remote.source.repository.RemoteBoothRepository
import com.beat.settingras.data.remote.source.repository.RemoteCommonRepository
import com.beat.settingras.data.remote.source.repository.RemoteSSLRepository
import org.apache.sshd.client.SshClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

private fun provideCommonApiService(retrofit: Retrofit): CommonAPI =
    retrofit.create(CommonAPI::class.java)

private fun provideBoothApiService(retrofit: Retrofit): BoothAPI =
    retrofit.create(BoothAPI::class.java)

private fun provideSSL(client: SshClient): SshClient{
    return client
}

val commonRepositoryModule = module {
    single { provideCommonApiService(get(named(Constant.KOINNAME.COMMON_NETWORK))) }
    single { provideBoothApiService(get(named(Constant.KOINNAME.BOOTH_NETWORK))) }
    single {
        provideSSL(get(named(Constant.KOINNAME.SSL)))
    }
    single { RemoteCommonRepository(get()) }
    single { RemoteBoothRepository(get()) }
    single {
        RemoteSSLRepository(get())
    }
}