package com.build.invertor.model.modelOld.json

interface DataAccesInterface {

    fun getFlagIndex() : Boolean
    fun getMaxUEID() : Int
    fun getList() : List<CardInventory>
    fun getLinkedList() : Map<String?, CardInventory>
    fun getListCode() : List<String>
    fun getPairLinkedMap() : Map<Pair<String,String>, CardInventory>
    fun getxDoubleLink() : Map<Pair<String,String>, MutableList<CardInventory>>
    fun updateIndexList()
}