package com.build.invertor.mainModule.camera

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.build.Invertor.R
import com.build.invertor.mainModule.AbstractController
import com.build.invertor.model.modelOld.json.CardInventory
import com.build.invertor.model.modelOld.json.JsonDownloader
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CameraController(private val fileDir : File, private val cacheDir : File) : AbstractController(fileDir,cacheDir) {

    private val data = this.getDataFile()
    private val json = this.getJsonFile()

    private val gson = GsonBuilder()
        .serializeNulls()
        .create()
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
        val newCacheFile = File(cacheDir,"${userName}.json")
        val jsonList = gson.toJson(card)

        try {
            writeCacheJsonToCacheDir(newCacheFile,jsonList)
        }catch (error : IOException){
            error.printStackTrace()
            Log.e("CacheFile","file doesn't save")
        }

    }

    private fun writeCacheJsonToCacheDir(cacheFile : File,jsonList : String)  {
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

    fun checkCard(card : List<CardInventory>) : StateCard{
        return when {
            card.isEmpty() -> StateCard.EMPTY
            card.size == 1 -> StateCard.ONE_ELEMENT
            card.size >= 2  -> StateCard.MULTIPLY_ELEMENTS
            else -> StateCard.EMPTY
        }
    }




}
