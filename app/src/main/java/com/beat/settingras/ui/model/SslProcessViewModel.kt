package com.beat.settingras.ui.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.beat.settingras.AppLog
import com.beat.settingras.R
import com.beat.settingras.data.remote.source.API
import com.beat.settingras.data.remote.source.repository.RemoteSSLRepository
import com.beat.settingras.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    fun sendMsgArray(key: String, isAuth: Boolean = false, isFinish: Boolean = false) {
        viewModelScope.launch {

            var msgArr: String? = setCmdMsgParam(key, isAuth)
            AppLog.d("msgArr : $msgArr")
            if (msgArr == "") {
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
                    if (isFinish) {
                        showToast(R.string.msg_reboot_logout)
                        finish.value = true
                    }
                }
        }
    }

    /**
     * bashProfile 읽기
     */
    fun readFile(key: String) {
        viewModelScope.launch {

            var msgArr: String? = setCmdMsgParam(key)
            AppLog.d("msgArr : $msgArr")
            if (msgArr == "") {
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

    fun downloadFile(key: String, subKey:String) {
        try {
            viewModelScope.launch {
                var msgArr: String? = setDownloadParam(key,subKey)
                var isLaunch:Boolean = true

                AppLog.d("msgArr : $msgArr")
                if (msgArr == "") {
                    showToast(R.string.error_connect)
                    return@launch
                }
                progressDialog.value = true
                while(isLaunch){
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
                            if(it.isNullOrEmpty()){
                                progressDialog.value = false
                                isLaunch = false
                                return@collect
                            }
                            else{
                                cmdText.value = it
                            }
                        }
                    msgArr="\n"
                    delay(5000)
                }
            }
        } catch (e:Exception){
            e.printStackTrace()
        }

    }

    /**
     * !@#$ 을 부스코드로 치환
     */
    private fun setCmdMsgParam(key: String, isAuth: Boolean = false): String? {
        try {
            var msgArr: String? = ""
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
                if (isAuth)
                    msgArr += password + "\n"
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

    /**
     * beat user 홈 폴더 이동 -> 상위 폴더 2번 이동 기본 -> filePath에 해당하는 폴더 생성
     * 해당 파일 삭제 -> 다운로드 반복
     */
    private fun setDownloadParam(key: String, subKey:String):String?{
        var msgArr: String? = ""
        try{
            var jsonObject: JSONObject? = cmdJson?.getJSONObject(key)?.getJSONObject(subKey)
            val filePath:String? = jsonObject?.getString("filepath")
            val fileArr:JSONArray? = jsonObject?.getJSONArray("filelist")
            msgArr += "cd ~beat\n"
            msgArr += "cd ..\n cd..\n"
            msgArr += "mkdir $filePath\n"
            if(fileArr != null){
                val len = fileArr.length()
                for (i in 0 until len) {
                    AppLog.d("jsonArr : ${fileArr.getString(i)}")
                    msgArr +="rm $filePath+${fileArr.getString(i)}\n"
                    msgArr += "wget -P /home/$filePath ${API.URL.BASE_AWS_URL+fileArr.getString(i)}\n"
                }
            }
            AppLog.d(msgArr.toString())
            return msgArr
        }
        catch (e:Exception){
            e.printStackTrace()
            return msgArr
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