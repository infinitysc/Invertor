package com.build.invertor.model.database.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserCard")
data class UserEntity (
    @ColumnInfo("id")
    @PrimaryKey(autoGenerate = false)
    var id : Int,
    @ColumnInfo("user")
    var user : String,
    @ColumnInfo("departament")
    var departament : String
)