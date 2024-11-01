package com.build.Invertor.model

import android.content.Context
import android.widget.Toast
import com.build.Invertor.model.csv.DataDownloader
import com.build.Invertor.model.json.JsonDownloader
import java.io.File

/**
 * Это класс для работы с данными
 * в него будет входить обновление данных
 * хранить он будет все нужные для работы списки а также классы
 * в планах тут попробовать реализовать ContentProvider(Для учебы)
 *
 * **/
class Model() {


    /*TODO :
    при первой инициализации класс Model должен быть пустым(когда файлов нет)
    в дальнейшем в первую очередь класс должен искать есть ли файлы если да то модель не пустой
    иначе мы должны его заполнить
    * пока еще думаю это делать либо фабрику либо билдер

    * */

    class Builder() {
        private val jsonFileName = "jso.json"
        private val dataFileName = "data.xlsx"

        private var checkFlag = false
        private var mContext : Context? = null
        private var jsonFile : JsonDownloader? = null
        private var dataFile : DataDownloader? = null


        fun installContext(mContext: Context) = apply {
            this.mContext = mContext
        }
        private fun jsonFileContains() = apply {
            val jsonFileLink = File(mContext?.filesDir,jsonFileName)
            if(jsonFileLink.exists()){
                this.jsonFile = JsonDownloader(jsonFileLink.inputStream())
            }
            else {
                checkFlag = true
            }
        }
        private fun dataFileContains() = apply{
            val dataFileLink = File(mContext?.filesDir,dataFileName)
            if(dataFileLink.exists()){
                this.dataFile = DataDownloader(dataFileLink.inputStream())
            }
            else {
                checkFlag = true
            }
        }
        fun build() : Model{
            jsonFileContains()
            dataFileContains()
            if(checkFlag){
               Toast.makeText(this.mContext,"Загрузите файлы с данными",Toast.LENGTH_SHORT).show()
            }
            else {
                return Model()
            }
            return Model()
        }
    }




}