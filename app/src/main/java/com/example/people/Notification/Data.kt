package com.example.people.Notification

import android.os.Parcel
import android.os.Parcelable

data class Data (
    private var user:String?="",
    private var icon: Int =0,
    private var body:String?="",
    private var title:String?="",
    private var sented:String?=""
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(user)
        parcel.writeInt(icon)
        parcel.writeString(body)
        parcel.writeString(title)
        parcel.writeString(sented)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Data> {
        override fun createFromParcel(parcel: Parcel): Data {
            return Data(parcel)
        }

        override fun newArray(size: Int): Array<Data?> {
            return arrayOfNulls(size)
        }
    }
}