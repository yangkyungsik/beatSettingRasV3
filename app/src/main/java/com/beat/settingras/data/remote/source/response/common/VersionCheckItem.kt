package com.beat.settingras.data.remote.source.response.common

import com.beat.settingras.data.remote.BaseItem
import com.google.gson.annotations.SerializedName

data class VersionCheckItem(@SerializedName("data") val data: VersionCheckData) :
    BaseItem() {
    data class VersionCheckData(
        @SerializedName("updatable") val updateAble: Boolean=false,
        @SerializedName("server_time") val serverTime: String?,
        @SerializedName("version_code") val versionCode: Int?=0,
        @SerializedName("update_time") val updateTime: String?
    )
}