package com.example.people.DataClass

import android.os.Parcel
import android.os.Parcelable

data class messageModel(
    var message: String? = "null",
    var senderId: String? = "null",
    var time:String?="null"
) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(message)
        parcel.writeString(senderId)
        parcel.writeString(time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<messageModel> {
        override fun createFromParcel(parcel: Parcel): messageModel {
            return messageModel(parcel)
        }

        override fun newArray(size: Int): Array<messageModel?> {
            return arrayOfNulls(size)
        }
    }
}