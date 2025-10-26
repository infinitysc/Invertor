package com.build.invertor.mainModule.Card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.build.invertor.model.database.Repository
import com.build.invertor.model.database.card.CardEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class CardViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val listSpin = listOf("","В эксплуатации","Требуется ремонт","Находится на консервации",
        "Не соответствует требованиям эксплуатации","Не введен в эксплуатацию","Списание","Утилизация")


     fun getCurrentTime(): String {
        val dateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        return "${dateTime.format(formatter)}+03"
    }



    fun getFlow(cardIndex : Int) : Flow<CardEntity?> {
        return flow { emit(repository.selectFromIndex(cardIndex)) }
    }

    fun updateCard(card : CardEntity) {
        viewModelScope.launch {
            repository.updateCard(card)
        }
    }

    fun addToDbNewCard(card : CardEntity) {
        viewModelScope.launch {
            repository.insert(card)
        }
    }



}
