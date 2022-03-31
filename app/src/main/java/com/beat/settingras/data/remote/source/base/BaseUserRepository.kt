package com.beat.settingras.data.remote.source.base

import com.beat.settingras.data.remote.source.response.common.SysInfoItem
import io.reactivex.Single

@Deprecated("test")
interface BaseUserRepository  : BaseRepository {
    fun getTest(params: MutableMap<String,Any>)
    fun requestSysInfo():Single<SysInfoItem>
}