package com.build.Invertor.model.json

import android.content.Context
import android.provider.MediaStore.Files
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.InputStream
import java.nio.charset.Charset

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
        pairLinkedMap = createDoubleLink(this.list)
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
    private fun createDoubleLink(list : List<CardInventory>) : Map<Pair<String,String>, CardInventory> {
        val startPairLinkerMap = mutableMapOf<Pair<String,String>, CardInventory>()

        for(card in list.iterator()){
            startPairLinkerMap.put(putPair(card.inventNumb,card.Cod1C),card)
        }

        return startPairLinkerMap
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
}