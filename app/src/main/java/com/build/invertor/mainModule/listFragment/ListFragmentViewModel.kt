package com.build.invertor.mainModule.listFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.build.invertor.model.database.Card
import com.build.invertor.model.database.Repository
import com.build.invertor.model.database.card.CardEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ListFragmentViewModel @Inject constructor(private val repository : Repository) : ViewModel(){


    private val data : MutableLiveData<List<CardEntity>> = MutableLiveData()
    val publicData : LiveData<List<CardEntity>> get() = data

    fun startWorking(code : String) {
        var check : List<CardEntity> = emptyList()
        viewModelScope.launch {
            searchFromCode(code).collect {
                check = it
            }
            data.value = check
        }
    }


    private fun searchFromCode(code : String) : Flow<List<CardEntity>>{
        return flow {
            emit(repository.getListCod1c(code))
        }
    }

}