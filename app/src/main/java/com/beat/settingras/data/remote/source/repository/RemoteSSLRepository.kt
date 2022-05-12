package com.beat.settingras.data.remote.source.repository

import android.util.Log
import com.beat.settingras.AppLog
import com.beat.settingras.data.remote.source.base.BaseSSLRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.channel.ClientChannel
import org.apache.sshd.client.channel.ClientChannelEvent
import org.apache.sshd.common.session.Session
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.TimeUnit

class RemoteSSLRepository(private val client: SshClient) : BaseSSLRepository {

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

    override suspend fun connect(): Flow<Boolean> = flow {
        try {
            client.start()
            var session = client?.run {
                this.connect(userName, ip, port).verify(10000).session
            }

            if (session != null) {
                session?.addPasswordIdentity(password)
                var auth = session?.auth()?.verify(5000)
                AppLog.d("auth status -> success : ${auth?.isSuccess}, failed : ${auth?.isFailure}, isDone : ${auth?.isDone}, canceled : ${auth?.isCanceled}")
            }
            AppLog.e("Connection establihed")

            responseStream = ByteArrayOutputStream()
            channel = session.createChannel(org.apache.sshd.common.channel.Channel.CHANNEL_SHELL).apply {
                setOut(responseStream)
                open().verify(5,TimeUnit.SECONDS)
            }
            emit(true)
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.e("error")
            emit(false)
        }
    }

    override suspend fun sendMsg(msg: String?): Flow<String> = flow {
        channel?.let {
            AppLog.d("isOpen : "+it.isOpen +"${it.streaming.toString()}")
            it.invertedIn.write(msg?.encodeToByteArray())
            it.invertedIn.flush()
        }

        delay(1000)

        if (responseStream != null) {
            val responseString = String(responseStream!!.toByteArray())
            Log.d(TAG, "response : ${responseString.trim()}")
            if(responseString.isNullOrEmpty() || responseString.isNullOrBlank()) {
                AppLog.d(TAG,"responseString isNullOrEmpty")
                emit("")
                return@flow
            }
            emit(responseString)
        } else {
            emit("")
        }
        responseStream?.reset()
    }

    override fun logout() {
        channel?.run {
            waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(5))
            close()
        }
    }

    companion object {
        val TAG = RemoteSSLRepository.javaClass.simpleName
    }
}