package com.beat.settingras.data.remote.source.response.common

import com.beat.settingras.data.remote.BaseItem
import com.google.gson.annotations.SerializedName

data class CenterVersionInfoItem(@SerializedName("data") val data: CenterVersionInfoData) :
    BaseItem() {
    data class CenterVersionInfoData(
        @SerializedName("download_url") val downloadUrl: String,
        @SerializedName("is_update") val isUpdate: Boolean,
        @SerializedName("version_code") val versionCode: Int
    )
}