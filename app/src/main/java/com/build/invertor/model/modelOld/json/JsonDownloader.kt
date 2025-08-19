package com.build.invertor.model.modelOld.json

import android.content.Context
import android.os.Parcelable

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.InputStream

class JsonDownloader(private val path : InputStream)  {

    private val gsonSerNulls = GsonBuilder()
        .serializeNulls()
        .create()
    private val gson : Gson = GsonBuilder().create()
    private var maxUEID = 0
    private val list : List<CardInventory>
    private val linkedMap : Map<String?, CardInventory>
    private val pairLinkedMap : Map<Pair<String,String>, CardInventory>
    private val listCod1cInvent : List<String>
    private var flagIndex = false

    private val xDoubleLink : Map<Pair<String,String>,MutableList<CardInventory>>

    init {
        list = createList()
        linkedMap = createLink(this.list)
        pairLinkedMap = createDoubleLink(this.list)
        listCod1cInvent = createListPair(this.list).toList()
        xDoubleLink = exp_createDoubleLink()
    }

   fun getFlagIndex() = flagIndex

    fun getMaxUEID() = maxUEID

   fun getList() : List<CardInventory> = this.list

    fun getLinkedList() : Map<String?, CardInventory> = this.linkedMap

    fun getListCode() : List<String> = this.listCod1cInvent

     fun getPairLinkedMap() : Map<Pair<String,String>, CardInventory> = this.pairLinkedMap

     fun getxDoubleLink() = this.xDoubleLink

     fun updateIndexList() {
        setIndexToCardInventory()
        maxUEID = setLast()
        flagIndex = true
    }


    fun updateListAfterIndex(context : Context) {
        if(flagIndex){
            val fileDir = context.filesDir
            val file = File(fileDir,"jso.json")
            val gson = GsonBuilder()
                .serializeNulls()
                .create()
            val str = gson.toJson(this.list)

            val cacheDir = context.cacheDir
            File(cacheDir,"max.txt")
            if(maxUEID > 0){
                FileWriter(File(cacheDir,"max.txt")).use {
                    it.write(maxUEID.toString())
                    it.flush()
                }
            }

            FileOutputStream(file).use {
                it.write(str.toByteArray())
                it.flush()
            }
        }
    }
    fun updateListAfterIndexController(cacheDir : File ,fileDir : File) {
        if(flagIndex){

            val file = File(fileDir,"jso.json")
            val gson = GsonBuilder()
                .serializeNulls()
                .create()
            val str = gson.toJson(this.list)

            val max = File(cacheDir,"max.txt")
            if(maxUEID > 0){
                FileWriter(max).use {
                    it.write(maxUEID.toString())
                    it.flush()
                }
            }
            FileOutputStream(file).use {
                it.write(str.toByteArray())
                it.flush()
            }

        }
    }

    private fun createList() : List<CardInventory> {
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

    private fun setIndexToCardInventory() : Unit {
        this.list.forEachIndexed { index, cardInventory ->
            cardInventory.index = index
        }
    }

    private fun setLast() : Int = this.list.last().index

    private fun createLink(list : List<CardInventory>): MutableMap<String?, CardInventory> {
        val link : MutableMap<String?, CardInventory> = mutableMapOf()

        for(i in list.iterator()){
            link[i.inventNumb] = i
        }

        return link
    }

    private fun createDoubleLink(list : List<CardInventory>) : Map<Pair<String,String>, CardInventory> {
        val startPairLinkerMap = mutableMapOf<Pair<String,String>, CardInventory>()

        for(card in list.iterator()){
            startPairLinkerMap[Pair(card.inventNumb ?: "", card.Cod1C ?: "")] = card
        }

        return startPairLinkerMap
    }

    private fun exp_createDoubleLink(): Map<Pair<String,String>, MutableList<CardInventory>> {
        val startPairLinkerMap = mutableMapOf<Pair<String,String>, MutableList<CardInventory>>()

        for(card in list.iterator()){
           keyContainsInMap(startPairLinkerMap,Pair(card.inventNumb ?:"",card.Cod1C ?:""),card)
        }

        return startPairLinkerMap
    }

    private fun keyContainsInMap(map : MutableMap<Pair<String,String>, MutableList<CardInventory>>, key : Pair<String,String>, value : CardInventory){
        if(key in map){
            map[key]?.add(value)
        }
        else {
            map[key] = mutableListOf(value)
        }
    }

    private fun createListPair(list : List<CardInventory>) : Set<String> {
        val startList = mutableSetOf<String>()

        for(card in list.iterator()){
            startList.add(card.inventNumb ?: "")
            startList.add(card.Cod1C ?: "")
        }
        return startList
    }


}