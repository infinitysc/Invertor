package com.build.invertor.mainModule.listFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.build.invertor.model.database.Repository
import com.build.invertor.model.database.card.CardEntity
import com.build.invertor.model.modelOld.json.json.CardInventory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ListFragmentViewModel @Inject constructor(private val repository : Repository) : ViewModel(){


    //TODO: запишу это в вьюмодель :
    // что нужно подумать над случаем если к нам добавляется новый элемент
    // через каллбек его решить или через условно datastore sharedpreferences

    fun getFlowListData(listIndex : List<Int>) : Flow<CardEntity>{
       return flow {
           for(index in listIndex) {
               emit(repository.selectFromIndex(index))
           }
       }
    }


}