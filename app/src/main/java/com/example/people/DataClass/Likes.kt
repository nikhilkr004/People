package com.example.people.DataClass

import android.os.Parcel
import android.os.Parcelable

data class Likes(
    val name:String?="null",
    val image:String?="null",
    val user:String?="null"
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(user)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Likes> {
        override fun createFromParcel(parcel: Parcel): Likes {
            return Likes(parcel)
        }

        override fun newArray(size: Int): Array<Likes?> {
            return arrayOfNulls(size)
        }
    }
}
