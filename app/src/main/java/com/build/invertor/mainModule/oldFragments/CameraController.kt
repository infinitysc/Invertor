package com.build.invertor.mainModule.oldFragments

import android.content.Context
import android.util.Log
import com.build.invertor.mainModule.oldFragments.AbstractController
import com.build.invertor.mainModule.camera.StateCard
import com.build.invertor.model.modelOld.json.json.CardInventory
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

class CameraController @Inject constructor(private val context : Context) : AbstractController(context) {

    private val data = this.getDataFile()
    private val json = this.getJsonFile()
    private val cacheDir : File = context.cacheDir


    private lateinit var gson : Gson

    @Inject
    @Named("GsonSerNulls")
    fun setGson(gson : Gson) {
        this.gson = gson
    }


    fun searchDataByNumber(id : String) : List<CardInventory> {
        val tempList = mutableListOf<CardInventory>()

        json!!.getxDoubleLink().forEach{ entry ->
            if(entry.key.first == id || entry.key.second == id)
                entry.value.forEach { value ->
                    tempList.add(value)
                }
        }

        return tempList
    }

    fun cacheSaver(card : List<CardInventory>, userName : String) {
        if(cacheContainsFiles()){
            cacheDeleteFiles()
        }
        val newCacheFile = File(context.cacheDir, "${userName}.json")
        val jsonList = gson.toJson(card)

        try {
            writeCacheJsonToCacheDir(newCacheFile,jsonList)
        }catch (error : IOException){
            error.printStackTrace()
            Log.e("CacheFile","file doesn't save")
        }

    }

    private fun writeCacheJsonToCacheDir(cacheFile : File, jsonList : String)  {
        FileOutputStream(cacheFile).use {
            it.write(jsonList.toByteArray())
            it.flush()
        }
        Log.i("CacheFile", "file saved in cache")
    }

    private fun cacheDeleteFiles() {
        cacheDir.listFiles()?.forEach {
            if(it.name != "max.txt"){
                it.delete()
            }
        }

    }
    private fun cacheContainsFiles() : Boolean{
        if(cacheDir.listFiles()!!.isEmpty()){
            return true
        }
        return false
    }

    fun checkCard(card : List<CardInventory>) : StateCard {
        return when {
            card.isEmpty() -> StateCard.EMPTY
            card.size == 1 -> StateCard.ONE_ELEMENT
            card.size >= 2  -> StateCard.MULTIPLY_ELEMENTS
            else -> StateCard.EMPTY
        }
    }




}