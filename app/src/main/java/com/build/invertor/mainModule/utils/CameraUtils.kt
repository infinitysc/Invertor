package com.build.invertor.mainModule.utils

import com.build.invertor.model.database.card.CardEntity
import com.build.invertor.model.database.card.Codes
import com.build.invertor.model.modelOld.json.json.CardInventory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

object CameraUtils {

    fun listCodesToListString(listCodes : List<Codes>) : List<String> {

       val mutableList : MutableList<String> = mutableListOf()

       listCodes.forEach { code ->
           mutableList.add(code.Cod1C ?: "")
           mutableList.add(code.inventNumb ?: "")
       }

       return mutableList
   }

}