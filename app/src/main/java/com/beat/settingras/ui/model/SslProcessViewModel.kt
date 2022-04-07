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
import org.json.JSONArray
import org.json.JSONObject

class SslProcessViewModel(private val repository: RemoteSSLRepository) : BaseViewModel() {

    var ip: String? = null
    var port: Int = 0
    var userName: String? = null
    var password: String? = null
    var storeCode: String? = null
    var cmdJson: JSONObject? = null

    var cmdText: MutableLiveData<String?> = MutableLiveData<String?>("")
    var readText: MutableLiveData<String?> = MutableLiveData<String?>("")

    fun init(
        ip: String?,
        port: Int,
        userName: String?,
        password: String?,
        storeCode: String?,
        cmdJson: JSONObject
    ) {
        this.ip = ip
        this.port = port
        this.userName = userName
        this.password = password
        this.storeCode = storeCode
        try {
            this.cmdJson = cmdJson
        } catch (e: Exception) {
            e.printStackTrace()
            showToast(R.string.error_connect)
            finish.value = true
        }
        progressDialog.value = true
        repository.setConnectInfo(ip, port, userName, password)

        viewModelScope.launch {
            repository.connect()
                .flowOn(Dispatchers.Default)
                .catch {
                    AppLog.d(TAG, "connect error")
                    showToast(R.string.error_connect)
                    progressDialog.value = false
                    finish.value = true
                }
                .collect {
                    progressDialog.value = false
                    showToast(R.string.success_connect)
                    AppLog.d(TAG, "connect $it")
                }
        }
    }

    fun sendMsgArray(key: String,isAuth:Boolean=false,isFinish:Boolean=false) {
        viewModelScope.launch {

            var msgArr: String? = setMsgParam(key,isAuth)
            AppLog.d("msgArr : $msgArr")
            if(msgArr == ""){
                showToast(R.string.error_connect)
                return@launch
            }

            progressDialog.value = true

            repository.sendMsg(msgArr)
                .flowOn(Dispatchers.Default)
                .catch {
                    AppLog.d(TAG, "connect error ${this.toString()}")
                    progressDialog.value = false
                    showToast(R.string.error_connect)
                    finish.value = true
                }
                .collect {
                    AppLog.d(TAG, "connect $it")
                    progressDialog.value = false
                    cmdText.value = it
                    if(isFinish){
                        showToast(R.string.msg_reboot_logout)
                        finish.value = true
                    }
                }
        }
    }

    /**
     * bashProfile 읽기
     */
    fun readFile(key:String) {
        viewModelScope.launch {

            var msgArr: String? = setMsgParam(key)
            AppLog.d("msgArr : $msgArr")
            if(msgArr == ""){
                showToast(R.string.error_connect)
                return@launch
            }
            progressDialog.value = true

            repository.sendMsg(msgArr)
                .flowOn(Dispatchers.Default)
                .catch {
                    AppLog.d(TAG, "connect error ${this.toString()}")
                    progressDialog.value = false
                    showToast(R.string.error_connect)
                    finish.value = true
                }
                .collect {
                    AppLog.d(TAG, "connect $it")
                    progressDialog.value = false
                    cmdText.value = it
                    readText.value = it
                }
        }
    }

    private fun setMsgParam(key:String,isAuth:Boolean=false):String?{
        try {
            var msgArr:String?=""
            var jsonArr: JSONArray? = cmdJson?.getJSONArray(key)
            if (jsonArr != null) {
                val len = jsonArr.length()
                for (i in 0 until len) {
                    AppLog.d("jsonArr : ${jsonArr.getString(i)}")
                    if (jsonArr.getString(i).contains("!@#$")) {
                        msgArr += jsonArr.getString(i).replace("!@#$", storeCode + "")
                    } else {
                        msgArr += jsonArr.getString(i) + "\n"
                    }
                }
                if(isAuth)
                    msgArr += password+"\n"
                return msgArr
            } else {
                showToast(R.string.error_connect)
                return ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showToast(R.string.error_connect)
            return ""
        }
    }
    override fun onCleared() {
        repository.logout()
        super.onCleared()
    }

    companion object {
        val TAG = SslProcessViewModel::class.java.simpleName
    }
}