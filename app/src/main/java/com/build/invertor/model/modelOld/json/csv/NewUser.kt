package com.build.invertor.model.modelOld.json.csv

import android.os.Parcel
import android.os.Parcelable


@Suppress("INFERRED_TYPE_VARIABLE_INTO_POSSIBLE_EMPTY_INTERSECTION")
data class NewUser(
    val user : User?,
    val cabinet : String,
    val adress : String,

    ): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(User::class.java.classLoader) ,
        parcel.readString() ?: "",
        parcel.readString() ?: "",
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cabinet)
        parcel.writeString(adress)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NewUser> {
        override fun createFromParcel(parcel: Parcel): NewUser {
            return NewUser(parcel)
        }

        override fun newArray(size: Int): Array<NewUser?> {
            return arrayOfNulls(size)
        }
    }

}