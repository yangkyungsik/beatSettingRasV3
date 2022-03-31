package com.beat.settingras.data.remote

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Meta : Parcelable {
    @SerializedName("code")
    var code: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("msg")
    var msg: String? = null

    override fun toString(): String {
        return "Meta{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", msg='" + msg + '\'' +
                '}'
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(code)
        dest.writeString(message)
        dest.writeString(msg)
    }

    constructor() {}
    protected constructor(`in`: Parcel) {
        code = `in`.readString()
        message = `in`.readString()
        msg = `in`.readString()
    }

    companion object {
        val CREATOR: Parcelable.Creator<Meta?> = object : Parcelable.Creator<Meta?> {
            override fun createFromParcel(source: Parcel): Meta? {
                return Meta(source)
            }

            override fun newArray(size: Int): Array<Meta?> {
                return arrayOfNulls(size)
            }
        }
    }
}