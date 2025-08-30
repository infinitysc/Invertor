package com.build.invertor.mainModule

import android.util.Log
import com.build.invertor.model.modelOld.json.csv.NewUser
import com.build.invertor.model.modelOld.json.json.CardInventory
import java.io.File
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

class DataControl constructor() : Control {


    private val mapKeyUser : MutableMap<String, NewUser> = mutableMapOf()
    private val mapKeyCard : MutableMap<String, List<CardInventory>> = mutableMapOf()

    override fun setKeyForUser(keyCreator : () -> String,user : NewUser) {
        mapKeyUser.put(keyCreator.invoke(),user)
    }

    override fun setKeyForCard(keyCreator: () -> String, card: List<CardInventory>) {
        mapKeyCard.put(keyCreator.invoke(),card)
    }

    override fun getElementFromUser(key: String): NewUser {
        if(mapKeyUser.containsKey(key)){
            return mapKeyUser.get(key) ?: NewUser(null,"null","null")
        }
        else {
            Log.i("Key","doesn't contains key")
            return NewUser(null,"null","null")
        }
    }

    override fun getListFromCard(key: String): List<CardInventory> {
        if(mapKeyCard.containsKey(key)){
            return mapKeyCard.get(key) ?: listOf()
        }
        else {
            Log.i("Key","doesn't contains key")
            return listOf()
        }
    }





}