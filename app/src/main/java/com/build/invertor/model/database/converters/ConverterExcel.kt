package com.build.invertor.model.database.converters

import android.content.Context
import com.build.invertor.model.modelOld.json.csv.DataDownloader
import com.build.invertor.model.database.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.InputStream

class ConverterExcel (inputStream: InputStream , private val context : Context){

    private val list = DataDownloader(inputStream).getList()
    private val repository = Repository.apply {
        initialize(context)
    }.get()
    init {
        list.let {
            runBlocking(Dispatchers.IO) {
                for(user in it!!)
                    repository.insertUser(user.toUserEntity())
            }

        }

    }
}