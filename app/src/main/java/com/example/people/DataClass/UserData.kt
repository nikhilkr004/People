package com.example.people.DataClass

import android.os.Parcel
import android.os.Parcelable

data class UserData(
    var name:String?=null,
    var email:String?=null,
    var userName:String?=null,
    var bio:String?=null,
    var userId:String?=null,
    var profileImage:String?=null,
    var professon:String?=null
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(userName)
        parcel.writeString(bio)
        parcel.writeString(userId)
        parcel.writeString(profileImage)
        parcel.writeString(professon)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserData> {
        override fun createFromParcel(parcel: Parcel): UserData {
            return UserData(parcel)
        }

        override fun newArray(size: Int): Array<UserData?> {
            return arrayOfNulls(size)
        }
    }

}
