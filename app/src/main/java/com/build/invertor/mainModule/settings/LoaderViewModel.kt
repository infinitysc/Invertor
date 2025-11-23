package com.build.invertor.mainModule.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.build.invertor.model.database.Repository
import com.build.invertor.model.database.card.CardEntity
import com.build.invertor.model.database.data.UserEntity
import com.build.invertor.model.converters.DataDownloader
import com.build.invertor.model.converters.JsonFileOpener
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class LoaderViewModel @Inject constructor(private val repository: Repository) : ViewModel(){

    fun createFromFileJsonList(file : File) : List<CardEntity> {
        return JsonFileOpener(file.inputStream(), GsonBuilder().create()).openForDatabase()
    }
    fun createFromFileExcelList(file : File) : List<UserEntity> {
        return DataDownloader(file.inputStream()).getListDb()
    }

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