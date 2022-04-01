package com.beat.settingras.data.remote.source.repository

import com.beat.settingras.AppLog
import com.beat.settingras.data.remote.source.base.BaseRepository
import org.apache.sshd.client.SshClient

class RemoteSSLRepository(private val client: SshClient) : AbstractBaseRepository(){

    fun start(){

    }
}