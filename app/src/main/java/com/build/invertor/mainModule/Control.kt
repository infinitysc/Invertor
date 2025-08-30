package com.build.invertor.mainModule

import com.build.invertor.model.modelOld.json.csv.NewUser
import com.build.invertor.model.modelOld.json.json.CardInventory

interface Control {

    fun setKeyForUser(keyCreator : () -> String,user : NewUser)
    fun setKeyForCard(keyCreator : () -> String,card : List<CardInventory>)

    fun getElementFromUser(key : String) : NewUser
    fun getListFromCard(key : String) : List<CardInventory>

}