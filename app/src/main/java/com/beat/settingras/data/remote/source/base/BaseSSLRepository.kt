package com.beat.settingras.data.remote.source.base

import kotlinx.coroutines.flow.Flow

interface BaseSSLRepository {
    fun setConnectInfo(ip: String?, port: Int, userName: String?, password: String?)
    suspend fun connect(): Flow<Boolean>
    suspend fun sendMsg(msg: String?): Flow<String>
    fun logout()
}