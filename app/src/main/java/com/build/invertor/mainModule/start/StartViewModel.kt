package com.build.invertor.mainModule.start

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.build.invertor.model.database.Repository
import com.build.invertor.model.database.data.UserEntity
import com.build.invertor.model.modelOld.json.csv.User
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import kotlin.system.measureTimeMillis

class StartViewModel @Inject constructor(private val repository: Repository) : ViewModel() {


    private val _departament : MutableLiveData<String> = MutableLiveData()
    val departament : LiveData<String> get() = _departament

    private val _items = MutableLiveData<List<UserEntity>>()

    private val _userFlow = MutableStateFlow<List<String>>(emptyList())
    val userFlow : Flow<List<String>> get() = _userFlow

    private val _conceteUser = MutableLiveData<UserEntity>()
    val concreteUser : LiveData<UserEntity> get() = _conceteUser

    init {
        getAllUser()
    }

    fun getListUserNamesFlow() {

        viewModelScope.launch {
            val list = repository.getUserNames()
            _userFlow.value = list
        }

    }

    fun getAllUser() {
        viewModelScope.launch {
            _items.value = repository.getAllUsers()
        }
    }




    fun searchDepartament(userName : String) {
        viewModelScope.launch {
           _departament.value = repository.selectUser(userName)?.departament
        }

    }

    fun getUser(userName : String) {
        viewModelScope.launch {
            _conceteUser.value = repository.selectUser(userName)
        }
    }

    fun checkData() : Boolean{
        if(_items.value?.isNotEmpty() == true){
            return true
        }
        return false
    }

    fun txtToList(input : InputStream) : List<String> {
        return try {
            BufferedReader(InputStreamReader(input)).use { reader ->
                reader.lineSequence().toList()
            }
        } catch (e : IOException){
            e.printStackTrace()
            listOf<String>()
        }
    }

}