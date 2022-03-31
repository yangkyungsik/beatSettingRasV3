package com.beat.settingras.data.remote.source.response.common

import com.beat.settingras.data.remote.BaseItem
import com.google.gson.annotations.SerializedName

data class VersionStatusItem(@SerializedName("data") val data: VersionCheckData) :
    BaseItem() {
    data class VersionCheckData(
        @SerializedName("updatable") val updateAble: Boolean=false
    )
}