package com.build.invertor.mainModule.Card

import android.content.Context
import android.view.View
import com.build.invertor.model.modelOld.json.json.CardInventory
import com.build.invertor.model.modelOld.json.csv.NewUser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Named
import kotlin.Int
import kotlin.collections.iterator
import kotlin.math.PI

class CardFragmentController @Inject constructor(
    private val context : Context,
    @Named("GsonSerNulls")private val gson : Gson
) {


    private var user : NewUser? = null
    private var card : CardInventory? = null

    fun setUser(user : NewUser) {
        this.user = user
    }
    fun setCard(card : CardInventory) {
        this.card = card
    }

    private val cacheDir : File = this.context.cacheDir


    suspend fun getMax() : Int {
        return withContext(Dispatchers.IO) {File(cacheDir,"max.txt").readText().toInt()}
    }

    suspend fun updateMax(maximumID : Int) {
        coroutineScope {
            launch(Dispatchers.IO) {
                FileWriter(File(cacheDir,"max.txt")).use {
                    it.write((maximumID))
                    it.flush()
                }
            }
        }
    }

    suspend fun saveDataFile(newCard : CardInventory,) {

        val fileName = "jso.json"
        val file = File(this@CardFragmentController.context.filesDir,fileName)
        val type = object : TypeToken<List<CardInventory>>() {}.type
        val work  = if(file.exists()){
            this@CardFragmentController.gson.fromJson<List<CardInventory>>(file.readText(),type)
        }else mutableListOf<List<CardInventory>>()

        for(i in work.iterator()){
            if((i as CardInventory).index.equals(newCard.index)){
                reWrite(i,newCard)
            }
        }

        val updatedJsonString = this@CardFragmentController.gson.toJson(work)
        try {
            this@CardFragmentController.context.openFileOutput(fileName,Context.MODE_PRIVATE).use {
                it.write(updatedJsonString.toByteArray())
                it.flush()
            }
        }catch (e : IOException){

        }
    }



    suspend fun getCacheFileToReWrite(fileNameFromCache : String,card : CardInventory) {

        val file = File(this.context.cacheDir,fileNameFromCache)
        if(file.exists()) {
            val type = object : TypeToken<List<CardInventory>>() {}.type
            val listData = this.gson.fromJson<List<CardInventory>>(file.readText(),type)

            for(i in listData.iterator()){
                if((i.index).equals(card.index)){
                    reWrite(i,card)
                }
            }

        }
    }
    private fun reWrite(oldCard : CardInventory, newCard : CardInventory) : CardInventory {
        oldCard.SID = newCard.SID
        oldCard.UEDescription = newCard.UEDescription
        oldCard.ActionDateTime = newCard.ActionDateTime
        oldCard.Adress = newCard.Adress
        oldCard.Status = newCard.Status
        oldCard.inventNumb = newCard.inventNumb
        oldCard.SerialNumb = newCard.SerialNumb
        oldCard.IsSNEdited = newCard.IsSNEdited
        oldCard.UserName = newCard.UserName
        oldCard.Description = newCard.Description
        oldCard.Cabinet = newCard.Cabinet
        oldCard.Cod1C = newCard.Cod1C
        oldCard.parentEqueipment = newCard.parentEqueipment
        return oldCard
    }


    private fun createNewCard(
        newSerialNumber : String,
        date : String,
        adress : String,
        staus : String,
        change : Int,
        cabinet : String ,
        note : String,
    ) : CardInventory {

        if(card != null && user != null){
            return CardInventory(
                index = card!!.index ,
                SID = card!!.SID ,
                UEID = card!!.UEID,
                UEDescription = card!!.UEDescription,
                ActionDateTime = date,
                Adress = adress,
                Status = staus,
                inventNumb = card!!.inventNumb,
                SerialNumb =newSerialNumber,
                IsSNEdited = change,
                UserName = "${user!!.user?.id}|${user!!.user?.userName}",
                Description = if(note == ""){null}else{note} ,
                Cabinet = cabinet,
                Cod1C = card!!.Cod1C,
                parentEqueipment = card!!.parentEqueipment,
                )
        }
        else {
            throw NullPointerException("card or user is not initialize")
        }
    }

    private fun getCurrentTime() : String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    }

}