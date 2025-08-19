package com.build.invertor.model.modelOld.json

import android.os.Parcel
import android.os.Parcelable


data class CardInventory(
    var index : Int ,
    var SID : Int ,
    var UEID : Int? ,
    var UEDescription : String?,
    var ActionDateTime : String?,
    var Adress : String?,
    var Status : String?,
    var inventNumb : String?,
    var SerialNumb : String?,
    var IsSNEdited : Int ,
    var UserName : String?,
    var Description : String?,
    var Cabinet : String?,
    var Cod1C : String?,
    var parentEqueipment : Int,
) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(index)
        parcel.writeInt(SID)
        parcel.writeValue(UEID)
        parcel.writeString(UEDescription)
        parcel.writeString(ActionDateTime)
        parcel.writeString(Adress)
        parcel.writeString(Status)
        parcel.writeString(inventNumb)
        parcel.writeString(SerialNumb)
        parcel.writeInt(IsSNEdited)
        parcel.writeString(UserName)
        parcel.writeString(Description)
        parcel.writeString(Cabinet)
        parcel.writeString(Cod1C)
        parcel.writeInt(parentEqueipment)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CardInventory> {
        override fun createFromParcel(parcel: Parcel): CardInventory {
            return CardInventory(parcel)
        }

        override fun newArray(size: Int): Array<CardInventory?> {
            return arrayOfNulls(size)
        }
    }


}