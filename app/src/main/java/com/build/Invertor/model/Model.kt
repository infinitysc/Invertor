package com.build.Invertor.model

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import com.build.Invertor.model.csv.DataDownloader
import com.build.Invertor.model.json.CardInventory
import com.build.Invertor.model.json.JsonDownloader
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.apache.poi.ss.formula.functions.T
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Это класс для работы с данными
 * в него будет входить обновление данных
 * хранить он будет все нужные для работы списки а также классы
 * в планах тут попробовать реализовать ContentProvider(Для учебы)
 *
 * **/
class Model constructor()
{

    private var jsonDownloader : JsonDownloader? = null
    private var dataDownloader : DataDownloader? = null
    private var context : Context? = null
    var flag = false
    private val typeTemplate = object : TypeToken<List<CardInventory>>() {}.type

    private val gsonEngine : Gson = GsonBuilder()
        .serializeNulls()
        .create()

    constructor(context : Context,jsonDownloader: JsonDownloader,dataDownloader: DataDownloader) : this(){
        this.context = context
        this.jsonDownloader = jsonDownloader
        this.dataDownloader = dataDownloader
    }


    fun checkFiles() : Boolean{
        if(jsonDownloader == null || dataDownloader == null){
            flag = true
        }
        return flag
    }


    private fun checkFile(fileName : String,directory : File,) : File? {
        val file = File(directory, fileName)
        return if (file.exists()) {
            file
        } else {
            Log.d(
                "FileWork",
                "Файл в заданной директории $directory,и с заданным именем $fileName не существует"
            )
            null
        }
    }




    fun cacheSaver(list : List<CardInventory>) {
        val cacheDir = this.context?.cacheDir
        val newCacheFile = File(cacheDir,"")
        val jsonList = this.gsonEngine.toJson(list)
        writeFile(newCacheFile,jsonList)
    }

    fun getCacheFileToReWrite(fileNameFromCache : String) : List<CardInventory> {
        val fileCacgeDir = this.context?.cacheDir
        val file = File(fileCacgeDir,fileNameFromCache)
        return fileToList(file)
    }

    fun reSaveDataFile(newCard: CardInventory,) {
        val fileDir = context?.filesDir

        val oldFile = File(fileDir,"")
        val work : List<CardInventory> = if(oldFile.exists()){
            gsonEngine.fromJson<List<CardInventory>>(oldFile.readText(),this.typeTemplate)
        }else{
            mutableListOf<CardInventory>()
        }
        for(i in work.iterator()) {
            if(i.UEID == newCard.UEID){
                reWriteCards_OldVers(i,newCard)
            }
        }
        writeFile(oldFile,this.gsonEngine.toJson(work))
    }

    private fun reWriteCards_OldVers(oldCard : CardInventory,newCard : CardInventory) : CardInventory{
        return oldCard.apply {
            SID = newCard.SID
            UEDescription = newCard.UEDescription
            ActionDateTime = newCard.ActionDateTime
            Adress = newCard.Adress
            Status = newCard.Status
            inventNumb = newCard.inventNumb
            SerialNumb = newCard.SerialNumb
            IsSNEdited = newCard.IsSNEdited
            UserName = newCard.UserName
            Description = newCard.Description
            Cabinet = newCard.Cabinet
            Cod1C = newCard.Cod1C
            parentEqueipment = newCard.parentEqueipment
        }
    }


    private fun reWriteCard_Opt(index : Int,card : CardInventory) {}

    private fun fileToList(file : File) : List<CardInventory> = this.gsonEngine.fromJson<List<CardInventory>>(file.readText(),this.typeTemplate)

    private fun writeFile(file : File,editedJsonString : String) {
        try {
            FileOutputStream(file).use {
                it.write(editedJsonString.toByteArray())
                it.flush()
            }
        }catch (ioError: IOException){
            ioError.printStackTrace()
            Log.d("FileWork","ioException")
        }
        catch (notFoundError: FileNotFoundException){
            Log.d("FileWork","файл для записи не был найден")
        }

    }

    fun addToEndJsonFile(newCard: CardInventory) {
        val file = File(this.context?.filesDir,"")
        val jsonList = fileToList(file).toMutableList()
        jsonList.add(newCard)
        writeFile(file,this.gsonEngine.toJson(jsonList))
    }


    fun updateJsonFile() {
        this.jsonDownloader = JsonDownloader(File(this.context?.filesDir,"jso.json").inputStream())
        Log.d("FileWork","Апдейт файла json")
    }

    fun getJsonData() : JsonDownloader?{
        return this.jsonDownloader
    }

    fun searchCards(code : String) : List<CardInventory>{ //need Update?
        val mutList : MutableList<CardInventory> = mutableListOf()

        val mapTemp = this.jsonDownloader?.exp_createDoubleLink()

        if(mapTemp != null){
            for(item in mapTemp.iterator()){
                if(item.key.second == code || item.key.first == code){
                    for(j in item.value.iterator()){
                        mutList.add(j)
                    }
                }
            }
        }

        return mutList
    }




    class Debug() {

        var context : Context? = null
        private fun debugSaveCache(card : CardInventory) {
            var file = File(this.context?.cacheDir,"")
            /*TODO: */
        }

        companion object dsl {
            fun build(deb : Debug.() -> Unit){}
        }
    }


    companion object Checker {
        private fun existFile(fileName: String,mContext: Context) : InputStream? {
            File(mContext.filesDir,fileName).apply {
                return if(this.exists()){
                    this.inputStream()
                }
                else {
                    null
                }
            }

        }
        fun getModel(mContexnt : Context) : Model?{
            val jsonInput : InputStream? = existFile("jso.json",mContexnt)
            val dataInput : InputStream? = existFile("data.xlsx",mContexnt)

            if(jsonInput != null && dataInput != null){
                Model(mContexnt, jsonDownloader = JsonDownloader(jsonInput), dataDownloader = DataDownloader(dataInput))
            }
            else {
                Model()
            }
            return null
        }

    }


    /*class SettingsModel(private val context : Context) {

        //private val exportJsonLauncher
        //private val importJsonLauncher
        fun importJsonFile(){



        }
        fun exportJsonFile(){

        }

        fun exportExcelFile(){

        }

    }*/



}