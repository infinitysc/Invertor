package com.build.invertor.model

import android.content.Context
import android.util.Log
import com.build.invertor.model.csv.DataDownloader
import com.build.invertor.model.json.CardInventory
import com.build.invertor.model.json.JsonDownloader
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
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



    fun reSaveDataFile(newCard: CardInventory,) {
        val fileDir = context?.filesDir

        val oldFile = File(fileDir,"")
        //tag:Model
        val work : List<CardInventory> = if(oldFile.exists()){
            gsonEngine.fromJson(oldFile.readText(),this.typeTemplate)
        }else{
            mutableListOf()
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



    private fun fileToList(file : File) : List<CardInventory> = this.gsonEngine.fromJson(file.readText(),this.typeTemplate)

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

    fun getJsonData() : JsonDownloader?{
        return this.jsonDownloader
    }

    fun searchCards(code : String) : List<CardInventory>{ //need Update?
        val mutList : MutableList<CardInventory> = mutableListOf()

        val mapTemp = this.jsonDownloader?.getxDoubleLink()

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





}