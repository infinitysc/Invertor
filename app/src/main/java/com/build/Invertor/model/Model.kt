package com.build.Invertor.model

import android.content.Context
import com.build.Invertor.model.csv.DataDownloader
import com.build.Invertor.model.json.JsonDownloader

/**
 * Это класс для работы с данными
 * в него будет входить обновление данных
 * хранить он будет все нужные для работы списки а также классы
 * в планах тут попробовать реализовать ContentProvider(Для учебы)
 *
 * **/
class Model(context : Context) {

    private val defaultJsonName = "jso.json"
    private val json : JsonDownloader? = null
    private val data : DataDownloader? = null
    private val defaultDataName = "data.xlsx"

}