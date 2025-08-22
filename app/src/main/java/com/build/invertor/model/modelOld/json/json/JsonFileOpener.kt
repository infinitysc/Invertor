package com.build.invertor.model.modelOld.json.json

import com.build.invertor.model.database.card.CardEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Named


class JsonFileOpener @Inject constructor(@Named("Json")private val path : InputStream, private val gson: Gson) {


    fun open () : List<CardInventory> {
        val jsonString = changeToString(path)
        val typeOfList = object : TypeToken<List<CardInventory>>() {}.type
        return gson.fromJson(jsonString,typeOfList)
    }

    private fun changeToString(input : InputStream) : String {
        val size = input.available()
        val buffer = ByteArray(size)
        input.read(buffer)
        input.close()
        val str = String(buffer, Charsets.UTF_8)
        if(str.isNotEmpty()){
            return str
        }
        else{
            return "empty"
        }
    }



    fun openForDatabase() : List<CardEntity> {
        val jsonString = changeToString(path)
        val typeOfList = object : TypeToken<List<CardEntity>>() {}.type
        return gson.fromJson(jsonString,typeOfList)
    }

}