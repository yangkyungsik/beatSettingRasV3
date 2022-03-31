package com.beat.settingras.ui.model

import androidx.lifecycle.MutableLiveData
import com.beat.settingras.data.remote.source.repository.UserRepository
import com.beat.settingras.data.remote.source.response.common.SysInfoItem
import com.beat.settingras.ui.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainViewModel(private val userRepository: UserRepository) : BaseViewModel(){

    val test = MutableLiveData<SysInfoItem>()

    fun doTest(){

        userRepository.requestSysInfo()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe{_refreshing.value = true}
            .doOnSuccess{_refreshing.value = false}
            .doOnError { _refreshing.value = false }
            .subscribe({
                test.value = it
            },{
                e -> e.printStackTrace()
            })

    }

    fun startActivity(){

    }

}