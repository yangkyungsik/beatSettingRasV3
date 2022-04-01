package com.beat.settingras.ui.model

import androidx.lifecycle.viewModelScope
import com.beat.settingras.data.remote.source.repository.RemoteSSLRepository
import com.beat.settingras.ui.BaseViewModel
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.channel.ClientChannel
import org.apache.sshd.server.forward.AcceptAllForwardingFilter

class SslProcessViewModel(private val repository:RemoteSSLRepository) : BaseViewModel() {

    var ip:String?=null
    var port:Int = 0
    var userName:String?=null
    var password:String?=null

    var channel:ClientChannel?=null
    var command:String?=null
    var client:SshClient? = null

    fun init(ip:String, port:Int, userName:String, password:String){
        this.ip = ip
        this.port = port
        this.userName = userName
        this.password = password

        repository.start()
    }

    fun connect(){

    }

}