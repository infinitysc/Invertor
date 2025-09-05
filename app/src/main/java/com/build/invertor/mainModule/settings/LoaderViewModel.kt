package com.build.invertor.mainModule.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.build.invertor.model.database.Repository
import com.build.invertor.model.database.card.CardEntity
import com.build.invertor.model.database.data.UserEntity
import com.build.invertor.model.modelOld.json.csv.DataDownloader
import com.build.invertor.model.modelOld.json.json.JsonFileOpener
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class LoaderViewModel @Inject constructor(private val repository: Repository) : ViewModel(){

    private fun createFromFileJsonList(file : File) : List<CardEntity> {
        return JsonFileOpener(file.inputStream(), GsonBuilder().create()).openForDatabase()
    }
    private fun createFromFileExcelList(file : File) : List<UserEntity> {
        return DataDownloader(file.inputStream()).getListDb()
    }


    fun loadFromFileJson(file : File) {
        val list = createFromFileJsonList(file)
        viewModelScope.launch {
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
    }


}