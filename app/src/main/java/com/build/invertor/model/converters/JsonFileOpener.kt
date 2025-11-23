package com.build.invertor.model.converters

import com.build.invertor.model.database.card.CardEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStream

class JsonFileOpener  constructor(private val path : InputStream, private val gson: Gson) {

    fun open () : List<CardEntity> {
        val jsonString = changeToString(path)
        val typeOfList = object : TypeToken<List<CardEntity>>() {}.type
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