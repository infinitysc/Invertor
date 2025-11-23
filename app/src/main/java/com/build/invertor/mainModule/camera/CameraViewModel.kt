package com.build.invertor.mainModule.camera

import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.build.invertor.mainModule.utils.CameraUtils
import com.build.invertor.model.database.Repository
import com.build.invertor.model.database.card.CardEntity
import com.build.invertor.model.database.card.Codes
import com.google.zxing.BarcodeFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject




class CameraViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val formats = listOf(
        BarcodeFormat.CODE_39,
        BarcodeFormat.CODE_128,
        BarcodeFormat.CODE_93,
        BarcodeFormat.EAN_13,
        BarcodeFormat.EAN_8,
    )

    private val _valueString : MutableLiveData<String> = MutableLiveData("defaultValue")
    val valueString : LiveData<String> get() = _valueString

    fun setNewValueTo(str : String) {
        _valueString.value = str

    }

    private val _m : MutableStateFlow<List<CardEntity>> = MutableStateFlow(emptyList())
    val m : StateFlow<List<CardEntity>> get() = _m.asStateFlow()

    fun createFlowData(str : String) {
        viewModelScope.launch {
            _m.value = repository.selectByString(str)
        }
    }


    val flo = flow {
        emit(repository.getListCode())
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList()
    )

    fun selectedByFlow(valueString : String) : Flow<List<CardEntity>> {
        return flow {
            emit(repository.selectByString(valueString))
        }
    }

    fun checkCard(card : List<CardEntity>) : StateCard{
        return when {
            card.isEmpty() -> StateCard.EMPTY
            card.size == 1 -> StateCard.ONE_ELEMENT
            card.size >= 2  -> StateCard.MULTIPLY_ELEMENTS
            else -> StateCard.EMPTY
        }
    }
}