package com.build.invertor.model.modelOld.json.csv

import android.os.Parcel
import android.os.Parcelable
import com.build.invertor.model.database.data.UserEntity

data class User(
    val id : Int ,
    val userName : String ,
    val departament : String

){

    fun toUserEntity() : UserEntity {
        return UserEntity(
            this.id,
            this.userName,
            this.departament
        )
    }
}
