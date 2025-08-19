package com.build.invertor.mainModule

import com.build.invertor.model.modelOld.json.csv.DataDownloader
import com.build.invertor.model.modelOld.json.JsonDownloader
import java.io.File

abstract class AbstractController(private val fileDir : File,private val cacheDir : File) {

    private val jsonFile : JsonDownloader?
    private val dataFile : DataDownloader?

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
            return JsonDownloader(file.inputStream()).apply {
                this.updateIndexList()
                this.updateListAfterIndexController(cacheDir,fileDir)
            }
        }catch (e : Exception){
            e.printStackTrace()
            return null
        }
    }


}