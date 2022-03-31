package com.beat.settingras.data.remote.source.response.booth

import com.google.gson.annotations.SerializedName

data class BoothIdItem(
    @SerializedName("result_code") var resultCode:String?,
    @SerializedName("result_message") var resultMessage:String?,
    @SerializedName("booth_id") var boothId:String?)