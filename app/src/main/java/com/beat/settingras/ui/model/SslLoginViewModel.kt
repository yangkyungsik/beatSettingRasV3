package com.beat.settingras.ui.model

import androidx.lifecycle.MutableLiveData
import com.beat.settingras.R
import com.beat.settingras.ui.BaseViewModel
import kotlin.coroutines.coroutineContext

class SslLoginViewModel : BaseViewModel(){

    val playerState = MutableLiveData<Boolean>(false)

    fun validateData(ip:String?, port:String?, username:String?, password:String?){
        when {
            ip.isNullOrEmpty() -> {
                playerState.value = false
                showToast(R.string.msg_null_ip)
                return
            }
            port.isNullOrEmpty() -> {
                playerState.value = false
                showToast(R.string.msg_null_port)
                return
            }
            username.isNullOrEmpty() -> {
                playerState.value = false
                showToast(R.string.msg_null_username)
                return
            }
            password.isNullOrEmpty() -> {
                playerState.value = false
                showToast(R.string.msg_null_pw)
                return
            }
        }
        playerState.value = true
    }
}