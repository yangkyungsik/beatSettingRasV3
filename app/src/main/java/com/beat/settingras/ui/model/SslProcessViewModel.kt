package com.beat.settingras.ui.model

import androidx.lifecycle.viewModelScope
import com.beat.settingras.AppLog
import com.beat.settingras.data.remote.source.repository.RemoteSSLRepository
import com.beat.settingras.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class SslProcessViewModel(private val repository:RemoteSSLRepository) : BaseViewModel() {

    var ip:String?=null
    var port:Int = 0
    var userName:String?=null
    var password:String?=null

    fun init(ip:String?, port:Int, userName:String?, password:String?){
        this.ip = ip
        this.port = port
        this.userName = userName
        this.password = password
        repository.setConnectInfo(ip,port,userName,password)

        viewModelScope.launch {
            repository.connect2()
                .flowOn(Dispatchers.Default)
                .catch {
                    AppLog.d(TAG,"connect error ${this.toString()}")
                    finish.value = true
                }
                .collect {
                    AppLog.d(TAG,"connect $it")
                }
        }
    }

    override fun onCleared() {
        repository.clear()
        super.onCleared()
    }

    companion object{
        val TAG = SslProcessViewModel::class.java.simpleName
    }
}