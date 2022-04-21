package com.beat.settingras.data.remote.source.repository

import android.util.Log
import com.beat.settingras.AppLog
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.channel.ClientChannel
import org.apache.sshd.client.channel.ClientChannelEvent
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.common.session.Session
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.TimeUnit

class RemoteSSLRepository(private val client: SshClient) : AbstractBaseRepository() {

    var session: Session? = null
    var channel: ClientChannel? = null

    private var ip: String? = null
    private var port: Int = 0
    private var userName: String? = null
    private var password: String? = null
    private var responseStream: ByteArrayOutputStream? = null

    fun setConnectInfo(ip: String?, port: Int, userName: String?, password: String?) {
        this.ip = ip
        this.port = port
        this.userName = userName
        this.password = password
    }

    suspend fun connect(): Flow<Boolean> = flow {
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

    suspend fun sendMsg(msg: String?): Flow<String> = flow {
//        if (msg.isNullOrEmpty()) {
//            emit("")
//            return@flow
//        }
        channel?.let {
            it.open().verify(5,TimeUnit.SECONDS)
            AppLog.d("isOpen : "+it.isOpen +"${it.streaming.toString()}")
            it.invertedIn.use { pipedIn ->
                pipedIn?.write(msg?.encodeToByteArray())
                pipedIn?.flush()
            }
        }

        delay(1000)
        if (responseStream != null) {
            val responseString = String(responseStream!!.toByteArray())
            Log.d(TAG, "response : $responseString")
            emit(responseString)
        } else {
            emit("")
        }
        responseStream?.reset()
    }

    fun logout() {
        channel?.run {
            waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(5))
            close()
        }
    }


    companion object {
        val TAG = RemoteSSLRepository.javaClass.simpleName
    }
}