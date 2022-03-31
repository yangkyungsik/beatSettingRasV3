package com.beat.settingras.data.remote.source.response.common

import com.beat.settingras.data.remote.BaseItem
import com.google.gson.annotations.SerializedName

data class SysInfoItem(@SerializedName("data") val data: SysInfoData) : BaseItem() {
    data class SysInfoData(
        @SerializedName("android_custom_url") val androidCustomUrl: String,
        @SerializedName("android_notice") val androidNotice: String,
        @SerializedName("android_update_type") val androidupdateType: Int,
        @SerializedName("android_version") val androidVersion: Int
    )
}