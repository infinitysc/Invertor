package com.build.invertor.mainModule.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.loader.content.Loader
import com.build.invertor.model.database.Repository
import com.build.invertor.model.database.card.CardEntity
import com.build.invertor.model.database.data.UserEntity
import com.build.invertor.model.modelOld.json.csv.DataDownloader
import com.build.invertor.model.modelOld.json.json.JsonFileOpener
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.reflect.KClass

class LoaderViewModel @Inject constructor(private val repository: Repository) : ViewModel(){

    fun createFromFileJsonList(file : File) : List<CardEntity> {
        return JsonFileOpener(file.inputStream(), GsonBuilder().create()).openForDatabase()
    }
    fun createFromFileExcelList(file : File) : List<UserEntity> {
        return DataDownloader(file.inputStream()).getListDb()
    }

    /*fun loadFromFileJson(file : File) {
        val list = createFromFileJsonList(file)

        viewModelScope.launch(job + Dispatchers.Default) {
            list.forEach { card ->
                repository.insert(card)
            }
        }
    }

    fun loadFromFileExcel(file : File) {
        val list = createFromFileExcelList(file)

        viewModelScope.launch {
            list.forEach { user ->
                repository.insertUser(user)
            }
        }
    }*/

    fun <R : Any> overLoad(file: File,obj : (File) -> List<R>){
        viewModelScope.launch {
            obj(file).forEach { data ->
                when(data) {
                    is UserEntity -> repository.insertUser(data)
                    is CardEntity -> repository.insert(data)
                 }
            }
        }
    }

}