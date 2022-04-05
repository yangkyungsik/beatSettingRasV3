package com.beat.settingras.ui.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.beat.settingras.AppLog
import com.beat.settingras.R
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
    var storeCode:String?=null
    var cmdText:MutableLiveData<String?>  = MutableLiveData<String?>("")

    fun init(ip:String?, port:Int, userName:String?, password:String?,storeCode:String?){
        this.ip = ip
        this.port = port
        this.userName = userName
        this.password = password
        this.storeCode = storeCode
        repository.setConnectInfo(ip,port,userName,password)

        viewModelScope.launch {
            repository.connect2()
                .flowOn(Dispatchers.Default)
                .catch {
                    AppLog.d(TAG,"connect error ${this.toString()}")
                    showToast(R.string.error_connect)
                    finish.value = true
                }
                .collect {
                    AppLog.d(TAG,"connect $it")
                }
        }
    }

    fun sendMsg(msg:String){
        viewModelScope.launch {
            repository.sendMsg(msg)
                .flowOn(Dispatchers.Default)
                .catch {
                    AppLog.d(TAG,"connect error ${this.toString()}")
                    showToast(R.string.error_connect)
                    finish.value = true
                }
                .collect {
                    AppLog.d(TAG,"connect $it")
                    cmdText.value = it
                }
        }
    }

    fun sendMsgArray(msg:Array<String>){
        viewModelScope.launch {
            var msgArr:String?=""

            for(i in msg.indices){
                if(msg[i].contains("!@#$")){
                    msg[i] = msg[i].replace("!@#$",storeCode+"")
                }
                msgArr+=msg[i]+"\n"
            }
            repository.sendMsg(msgArr)
                .flowOn(Dispatchers.Default)
                .catch {
                    AppLog.d(TAG,"connect error ${this.toString()}")
                    showToast(R.string.error_connect)
                    finish.value = true
                }
                .collect {
                    AppLog.d(TAG,"connect $it")
                    cmdText.value = it
                }
        }
    }

    override fun onCleared() {
        repository.logout()
        super.onCleared()
    }

    companion object{
        val TAG = SslProcessViewModel::class.java.simpleName
    }
}