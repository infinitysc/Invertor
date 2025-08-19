package com.build.invertor.model.database.converters

import android.content.Context
import com.build.invertor.model.database.Repository
import com.build.invertor.model.database.card.CardEntity
import com.build.invertor.model.modelOld.json.JsonFileOpener
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.InputStream

class ConverterJson(
    input : InputStream,
    context: Context
) {
    private val gson = GsonBuilder().create()
    private val jsonOpener = JsonFileOpener(input,gson)
    init {
        val list : List<CardEntity> = jsonOpener.openForDatabase()
        val repository = Repository.apply {
            initialize(context)
        }.get()
        runBlocking(Dispatchers.IO){
            list.forEach{ card ->
                repository.insert(card)
            }
        }
    }


}