package com.beat.settingras.ui

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {

    val _refreshing: MutableLiveData<Boolean> = MutableLiveData(false)
    val refreshing: MutableLiveData<Boolean>
        get() = _refreshing

    val _finish: MutableLiveData<Boolean> = MutableLiveData(false)
    val finish: MutableLiveData<Boolean>
        get() = _finish

    val _toastString: MutableLiveData<String> = MutableLiveData("")
    val toastString: MutableLiveData<String>
        get() = _toastString

    val _toastInt: MutableLiveData<Int> = MutableLiveData(-1)
    val toastInt: MutableLiveData<Int>
        get() = _toastInt

    val _newIntent: MutableLiveData<Intent> = MutableLiveData()
    val newIntent: MutableLiveData<Intent>
        get() = _newIntent

    val _finishIntent: MutableLiveData<Intent> = MutableLiveData()
    val finishIntent: MutableLiveData<Intent>
        get() = _finishIntent

    fun showToast(msg: String?) {
        toastString.value = msg
    }

    fun showToast(msg: Int) {
        toastInt.value = msg
    }
}