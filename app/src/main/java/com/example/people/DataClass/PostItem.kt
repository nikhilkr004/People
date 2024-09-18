package com.example.people.DataClass

import android.os.Parcel
import android.os.Parcelable

data class PostItem(
    val name: String? = "null",
    val time: String? = "null",
    val title:String?= "null",
    val image:String?="null",
    val userID: String? = "null",
    var postImage: String? ="null",
    var like:Int= 0,
    var comment:Int= 0,
    var postID: String? = "null",

):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(time)
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeString(userID)
        parcel.writeString(postImage)
        parcel.writeInt(like)
        parcel.writeInt(comment)
        parcel.writeString(postID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PostItem> {
        override fun createFromParcel(parcel: Parcel): PostItem {
            return PostItem(parcel)
        }

        override fun newArray(size: Int): Array<PostItem?> {
            return arrayOfNulls(size)
        }
    }
}