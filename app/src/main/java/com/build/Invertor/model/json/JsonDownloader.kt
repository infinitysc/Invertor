package com.build.Invertor.model.json

import android.content.Context
import android.util.Log

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.InputStream


/**
 * Класс предназначенный для загрузки json файла из системы Android(пока не реализованно)
 *
 * Происходит преобразование json строки в List<CardInventory>
 * и разборка этого списка на:
 *
 * Список,
 *
 * "Связанный" список invetNumber(Инвентарный номер) -> CardInventory**
 *
 * "Связанный" список(map) в виде Pair<inventNumb,cod1C>(пара инвентарного номера и кода 1с) -> CardInventory
 *
 * **/
class JsonDownloader(private val path : InputStream) {


    private val gson : Gson = GsonBuilder().create()
    private val list : List<CardInventory>
    private val linkedMap : Map<String?, CardInventory>
    private val pairLinkedMap : Map<Pair<String,String>, CardInventory>
    private val listCod1cInvent : List<String>
    init {
        val jsonString = changeToString(path)
        val typeOfList = object : TypeToken<List<CardInventory>>() {}.type
        list = gson.fromJson(jsonString,typeOfList)
        linkedMap = createLink(this.list)
        pairLinkedMap = createDoubleLink()
        listCod1cInvent = createListPair(this.list).toList()
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

    /**Получаем список CardInventory**/
    fun getList() : List<CardInventory> = this.list

    /** Получаем "связанный" список invetNumber(Инвентарный номер) -> CardInventory**/
    fun getLinkedList() : Map<String?, CardInventory> = this.linkedMap

    fun getListCode() : List<String> = this.listCod1cInvent

    /** Создает "связанный" список(map) invetNumber(Инвентарный номер) -> CardInventory**/
    private fun createLink(list : List<CardInventory>): MutableMap<String?, CardInventory> {
        val link : MutableMap<String?, CardInventory> = mutableMapOf()
        for(i in list.iterator()){
            link.put(i.inventNumb,i)
        }
        return link
    }

    fun getPairLinkedMap() : Map<Pair<String,String>, CardInventory> = this.pairLinkedMap
    /**создает "связанный" список(map) в виде Pair<inventNumb,cod1C>(пара инвентарного номера и кода 1с) -> CardInventory**/
    private fun createDoubleLink() : Map<Pair<String,String>, CardInventory> {
        val startPairLinkerMap = mutableMapOf<Pair<String,String>, CardInventory>()

        var lists : MutableList<CardInventory> = mutableListOf()
        for(card in list.iterator()){

            startPairLinkerMap[Pair(card.inventNumb ?: "", card.Cod1C ?: "")] = card
        }

        Log.d("debugPairLinkedMap","$lists")
        return startPairLinkerMap
    }
     fun exp_createDoubleLink(): Map<Pair<String,String>, MutableList<CardInventory>> {
        val startPairLinkerMap = mutableMapOf<Pair<String,String>, MutableList<CardInventory>>()


        for(card in list.iterator()){
           addToMap(startPairLinkerMap,Pair(card.inventNumb ?:"",card.Cod1C ?:""),card)
        }

        return startPairLinkerMap
    }
    private fun addToMap(map : MutableMap<Pair<String,String>, MutableList<CardInventory>>,key : Pair<String,String>,value : CardInventory){
        if(key in map){
            map[key]?.add(value)
        }else {
            map[key] = mutableListOf(value)
        }
    }
    fun debugToCache(context : Context) {
        val neFile = File(context.cacheDir, "debug.txt")
        neFile.bufferedWriter().use { out ->
            pairLinkedMap.forEach {
                val line = "(${it.key.first},${it.key.second} -> ${it.value})\n"
                out.write(line)
            }
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
    /** Создает пару и если значения равны нулю то возвращает пустую строку **/
    private fun putPair(inventNumber : String?, code : String?) : Pair<String,String> = Pair(inventNumber ?: "", code ?: "")

    fun searchMaxUEID() : Int {
        var max = 0
        for(i in list.iterator()){
            if(i.UEID != null){
                if(i.UEID!! > max) {
                    max = i.UEID!!
                }
            }
        }
        return max
    }

}