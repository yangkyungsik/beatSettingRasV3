package com.beat.settingras.ui.model

import androidx.lifecycle.MutableLiveData
import com.beat.settingras.R
import com.beat.settingras.ui.BaseViewModel

class SslLoginViewModel : BaseViewModel(){

    val validate = MutableLiveData<Boolean>(false)

    fun validateData(ip:String?, port:String?, username:String?, password:String?,storeCode:String?){
        when {
            ip.isNullOrEmpty() -> {
                validate.value = false
                showToast(R.string.msg_null_ip)
                return
            }
            port.isNullOrEmpty() -> {
                validate.value = false
                showToast(R.string.msg_null_port)
                return
            }
            username.isNullOrEmpty() -> {
                validate.value = false
                showToast(R.string.msg_null_username)
                return
            }
            password.isNullOrEmpty() -> {
                validate.value = false
                showToast(R.string.msg_null_pw)
                return
            }
            storeCode.isNullOrEmpty() -> {
                validate.value = false
                showToast(R.string.msg_null_pw)
                return
            }
        }
        validate.value = true
    }
}