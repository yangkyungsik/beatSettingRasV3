package com.beat.settingras.data.remote

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

open class BaseItem() : Parcelable {

    @SerializedName("meta")
    lateinit var meta: Meta

    constructor(parcel: Parcel) : this() {
        //meta = parcel.readParcelable(Meta::class.java.classLoader)
        meta = parcel.readParcelable<Meta>(Meta::class.java.classLoader)!!
    }

    override fun toString(): String {
        return super.toString()
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeParcelable(this.meta,flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BaseItem> {
        override fun createFromParcel(parcel: Parcel): BaseItem {
            return BaseItem(parcel)
        }

        override fun newArray(size: Int): Array<BaseItem?> {
            return arrayOfNulls(size)
        }
    }


}