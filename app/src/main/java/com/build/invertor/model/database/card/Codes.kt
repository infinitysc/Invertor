package com.build.invertor.model.database.card

import androidx.room.ColumnInfo

data class Codes(
    @ColumnInfo("InventNumb")
    val inventNumb : String?,
    @ColumnInfo("Code1C")
    val Cod1C : String?,
){}