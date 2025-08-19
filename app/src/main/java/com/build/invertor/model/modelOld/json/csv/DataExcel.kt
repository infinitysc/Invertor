package com.build.invertor.model.modelOld.json.csv

interface DataExcel {

    fun getList() : List<User>?

    fun createLinkedMap() : Map<String, User>

    fun createIdLinkedMap() : Map<String, User>

}