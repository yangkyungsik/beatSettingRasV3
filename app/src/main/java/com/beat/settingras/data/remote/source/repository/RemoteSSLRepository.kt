package com.beat.settingras.data.remote.source.repository

import android.util.Log
import com.beat.settingras.AppLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.channel.ClientChannel
import org.apache.sshd.client.channel.ClientChannelEvent
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

    fun setConnectInfo(ip: String?, port: Int, userName: String?, password: String?) {
        this.ip = ip
        this.port = port
        this.userName = userName
        this.password = password
    }

    suspend fun connect2(): Flow<Boolean> = flow {
        try {
            try {
                client.start()
                var session = client?.run {
                    this.connect(userName, ip, port).verify(10000).session
                }
                if (session != null) {
                    session?.addPasswordIdentity(password)
                    session?.auth()?.verify(5000)
                }
                AppLog.e("Connection establihed")

                channel =
                    session?.createChannel(org.apache.sshd.common.channel.Channel.CHANNEL_SHELL)
                var responseStream = ByteArrayOutputStream()
                channel?.setOut(responseStream)

                channel?.open()?.verify(5, TimeUnit.SECONDS)
                channel?.invertedIn.use { pipedIn ->
                    pipedIn?.write("java -version\n".toByteArray())
                    pipedIn?.flush()
                }
                channel?.waitFor(
                    EnumSet.of(ClientChannelEvent.CLOSED),
                    TimeUnit.SECONDS.toMillis(5)
                );
                val responseString = String(responseStream.toByteArray())
                Log.d("SSH", responseString)
                emit(true)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                AppLog.e("error")
                emit(false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(false)
        }
    }

    fun connect() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                try {
                    client.start()
                    var session = client?.run {
                        this.connect(userName, ip, port).verify(10000).session
                    }
                    if (session != null) {
                        session?.addPasswordIdentity(password)
                        session?.auth()?.verify(5000)
                    }
                    AppLog.e(TAG,"Connection establihed")

                    channel =
                        session?.createChannel(org.apache.sshd.common.channel.Channel.CHANNEL_SHELL)
                    var responseStream = ByteArrayOutputStream()
                    channel?.setOut(responseStream)

                    channel?.open()?.verify(5, TimeUnit.SECONDS)
                    channel?.invertedIn.use { pipedIn ->
                        pipedIn?.write("java -version\n".toByteArray())
                        pipedIn?.flush()
                    }
                    channel?.waitFor(
                        EnumSet.of(ClientChannelEvent.CLOSED),
                        TimeUnit.SECONDS.toMillis(5)
                    );
                    val responseString = String(responseStream.toByteArray())
                    Log.d(TAG, responseString)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    AppLog.e(TAG,"error")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clear(){
        channel = null
    }



    companion object{
        val TAG = RemoteSSLRepository.javaClass.simpleName
    }
}