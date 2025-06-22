package com.build.invertor.model.json


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
)
