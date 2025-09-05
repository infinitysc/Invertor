package com.build.invertor.model.database.card

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Card")
data class CardEntity (

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("id")
    val index : Int ,
    @ColumnInfo("SID")
    var SID : Int ,
    @ColumnInfo("UEID")
    var UEID : Int? ,
    @ColumnInfo("UEDescription")
    var UEDescription : String?,
    @ColumnInfo("ActionDateTime")
    var ActionDateTime : String?,
    @ColumnInfo("Adress")
    var Adress : String?,
    @ColumnInfo("Status")
    var Status : String?,
    @ColumnInfo("InventNumb")
    var inventNumb : String?,
    @ColumnInfo("SerialNumb")
    var SerialNumb : String?,
    @ColumnInfo("IS_SN_EDITED")
    var IsSNEdited : Int ,
    @ColumnInfo("UserName")
    var UserName : String?,
    @ColumnInfo("Description")
    var Description : String?,
    @ColumnInfo("Cabinet")
    var Cabinet : String?,
    @ColumnInfo("Code1C")
    var Cod1C : String?,
    @ColumnInfo("ParentEqueipment")
    var parentEqueipment : Int,



)