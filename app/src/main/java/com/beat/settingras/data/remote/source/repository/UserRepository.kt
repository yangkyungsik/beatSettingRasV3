package com.beat.settingras.data.remote.source.repository

import com.beat.settingras.data.remote.source.base.BaseUserRepository
import com.beat.settingras.data.remote.source.response.common.SysInfoItem
import io.reactivex.Single

@Deprecated("test")
class UserRepository(private val remoteUserRepository: RemoteUserRepository) : BaseUserRepository {


    override fun getTest(params: MutableMap<String, Any>) {

    }

    override fun requestSysInfo(): Single<SysInfoItem> {
        return remoteUserRepository.requestSysInfo()
    }

    override fun getDefaultHeader(): MutableMap<String, Any?> {
        TODO("Not yet implemented")
    }

    override fun getDefaultHeader(isOAuthRequest: Boolean): MutableMap<String, Any?> {
        TODO("Not yet implemented")
    }

}