package com.build.invertor.mainModule.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.build.invertor.model.database.Repository
import com.build.invertor.model.database.card.CardEntity
import com.build.invertor.model.modelOld.json.csv.NewUser
import com.google.android.material.textfield.TextInputEditText
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class CardViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val listSpin = listOf("","В эксплуатации","Требуется ремонт","Находится на консервации",
        "Не соответствует требованиям эксплуатации","Не введен в эксплуатацию","Списание","Утилизация")

    val decoderFactory = DefaultDecoderFactory(listOf(
        BarcodeFormat.CODE_93,
        BarcodeFormat.CODE_128,
        BarcodeFormat.CODE_39,
        BarcodeFormat.EAN_8,
        BarcodeFormat.EAN_13,
        BarcodeFormat.QR_CODE,
        BarcodeFormat.ITF,
        BarcodeFormat.UPC_EAN_EXTENSION,
        BarcodeFormat.UPC_E,
        BarcodeFormat.UPC_A,
        BarcodeFormat.CODABAR
    ))
     fun getCurrentTime(): String {
        val dateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        return "${dateTime.format(formatter)}+03"
    }

    fun createNewCard(card : CardEntity?,
                      user : NewUser?,
                      elements : ElementsCard ) : CardEntity{
        return CardEntity(
            index = 0,
            SID =  card?.SID!!,
            UEID = null ,
            UEDescription = if(elements.descriptionUE == ""){null}else {elements.descriptionUE},
            ActionDateTime = getCurrentTime(),
            Adress = card.Adress,
            Status = elements.spinPos,
            inventNumb = card.inventNumb,
            SerialNumb = if(elements.serialNumber == ""){null}else{elements.serialNumber},
            IsSNEdited = checkChangeSerialNumber(elements.serialNumber),
            UserName = "${user?.user?.id}|${user?.user?.userName}",
            Description = if(elements.description == ""){null}else{elements.description},
            Cabinet = user?.cabinet,
            Cod1C = card.Cod1C,
            parentEqueipment = card.UEID ?: 0
        )
    }
    private fun checkChangeSerialNumber(serialNumber : String) : Int {
        return if(serialNumber!= ""){
            1
        } else 0
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

data class ElementsCard(
    val descriptionUE : String,
    val spinPos : String,
    val serialNumber : String,
    val description : String,
)
