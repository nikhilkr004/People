package com.example.people.DataClass

import android.os.Parcel
import android.os.Parcelable

data class NotesData(
    val name:String?="null",
    val userId:String?="null",
    val userImage:String?="null",
    val notes:String?="null",
    val time:String?="null"
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(userId)
        parcel.writeString(userImage)
        parcel.writeString(notes)
        parcel.writeString(time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NotesData> {
        override fun createFromParcel(parcel: Parcel): NotesData {
            return NotesData(parcel)
        }

        override fun newArray(size: Int): Array<NotesData?> {
            return arrayOfNulls(size)
        }
    }
}
