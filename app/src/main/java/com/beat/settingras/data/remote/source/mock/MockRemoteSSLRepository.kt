package com.beat.settingras.data.remote.source.mock

import com.beat.settingras.data.remote.source.base.BaseSSLRepository
import kotlinx.coroutines.flow.Flow
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.channel.ClientChannel
import org.apache.sshd.common.session.Session
import java.io.ByteArrayOutputStream

class MockRemoteSSLRepository(client: SshClient) : BaseSSLRepository {
    var session: Session? = null
    var channel: ClientChannel? = null

    private var ip: String? = null
    private var port: Int = 0
    private var userName: String? = null
    private var password: String? = null
    private var responseStream: ByteArrayOutputStream? = null

    override fun setConnectInfo(ip: String?, port: Int, userName: String?, password: String?) {
        this.ip = ip
        this.port = port
        this.userName = userName
        this.password = password
    }

    override suspend fun connect(): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun sendMsg(msg: String?): Flow<String> {
        TODO("Not yet implemented")
    }

    override fun logout() {
        TODO("Not yet implemented")
    }
}