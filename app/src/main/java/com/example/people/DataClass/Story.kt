package com.example.people.DataClass

import android.os.Parcel
import android.os.Parcelable

data class Story(

    val imageUrl:String?="null",
    val timeStart:Long=0,
    val timeEnd:Long=0,
    val storyId:String?="null",
    val  userId:String?="null"
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageUrl)
        parcel.writeLong(timeStart)
        parcel.writeLong(timeEnd)
        parcel.writeString(storyId)
        parcel.writeString(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Story> {
        override fun createFromParcel(parcel: Parcel): Story {
            return Story(parcel)
        }

        override fun newArray(size: Int): Array<Story?> {
            return arrayOfNulls(size)
        }
    }

}
