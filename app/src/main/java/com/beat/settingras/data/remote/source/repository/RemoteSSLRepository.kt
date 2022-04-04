package com.beat.settingras.data.remote.source.repository

import android.util.Log
import com.beat.settingras.AppLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    lateinit var channel: ClientChannel

    private var ip: String? = null
    private var port: Int = 0
    private var userName: String? = null
    private var password: String? = null
    private var responseStream:ByteArrayOutputStream? = null

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

                channel = session.createChannel(org.apache.sshd.common.channel.Channel.CHANNEL_SHELL)
                responseStream = ByteArrayOutputStream()
                channel?.setOut(responseStream)

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

    suspend fun sendMsg(msg:String?):Flow<String>  = flow{
        if(msg.isNullOrEmpty()) {
            emit("")
            return@flow
        }

        channel.open()?.verify(5, TimeUnit.SECONDS)

        channel.invertedIn.use { pipedIn ->
            pipedIn?.write(msg?.encodeToByteArray())
            pipedIn?.flush()
        }

        delay(1000)
        if(responseStream != null) {
            val responseString = String(responseStream!!.toByteArray())
            Log.d(TAG, "response : $responseString")
            emit(responseString)
        }else{
            emit("")
        }
        responseStream?.reset()
    }

//    fun connect() {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                try {
//                    client.start()
//                    var session = client?.run {
//                        this.connect(userName, ip, port).verify(10000).session
//                    }
//                    if (session != null) {
//                        session?.addPasswordIdentity(password)
//                        session?.auth()?.verify(5000)
//                    }
//                    AppLog.e(TAG,"Connection establihed")
//
//                    channel = session.createChannel(org.apache.sshd.common.channel.Channel.CHANNEL_SHELL)
//                    var responseStream = ByteArrayOutputStream()
//                    channel?.setOut(responseStream)
//
//                    channel?.open()?.verify(5, TimeUnit.SECONDS)
//                    channel?.invertedIn.use { pipedIn ->
//                        pipedIn?.write("java -version\n".toByteArray())
//                        pipedIn?.flush()
//                    }
//                    channel?.waitFor(
//                        EnumSet.of(ClientChannelEvent.CLOSED),
//                        TimeUnit.SECONDS.toMillis(5)
//                    );
//                    val responseString = String(responseStream.toByteArray())
//                    Log.d(TAG, responseString)
//                } catch (e: java.lang.Exception) {
//                    e.printStackTrace()
//                    AppLog.e(TAG,"error")
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }

    fun logout(){
        channel.run {
            waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(5))
            close()
        }
        channel?.close()
    }



    companion object{
        val TAG = RemoteSSLRepository.javaClass.simpleName
    }
}