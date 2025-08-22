package com.build.invertor.mainModule

import android.content.Context
import com.build.invertor.model.modelOld.json.csv.DataDownloader
import com.build.invertor.model.modelOld.json.json.JsonDownloader
import com.build.invertor.model.modelOld.json.json.JsonFileOpener
import com.google.gson.GsonBuilder
import java.io.File

abstract class AbstractController(context : Context) {

    private val jsonFile : JsonDownloader?
    private val dataFile : DataDownloader?

    private val fileDir = context.filesDir
    private val cacheDir = context.cacheDir


    init {
        dataFile = searchDataOld()
        jsonFile = setJsonOld()
    }

    fun getJsonFile() = jsonFile
    fun getDataFile() = dataFile

    private fun searchDataOld() : DataDownloader? {
        val fileName = "data.xlsx"
        try {
            val file = File(fileDir,fileName)
            if(file.exists()){
                return DataDownloader(file.inputStream())
            }else {
                return null
            }
        }catch (e : Exception){
            e.printStackTrace()
            return null
        }
    }
    private fun setJsonOld() : JsonDownloader? {
        val fileName = "jso.json"

        try {
            val file = File(fileDir,fileName)
            return JsonDownloader(JsonFileOpener(file.inputStream(), gson = GsonBuilder().create())).apply {
                this.updateIndexList()
                this.updateListAfterIndexController(cacheDir,fileDir)
            }
        }catch (e : Exception){
            e.printStackTrace()
            return null
        }
    }


}