package com.build.invertor.mainModule.Card

import com.build.invertor.mainModule.AbstractController
import com.build.invertor.model.modelOld.json.CardInventory
import com.build.invertor.model.modelOld.json.csv.NewUser
import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CardFragmentController(
    private val cacheDir : File,
    private val card : CardInventory,
    private val user : NewUser
) {


    fun getMax() : Int {
        return File(cacheDir,"max.txt").readText().toInt()
    }

    fun updateMax(maximumID : Int) {
        FileWriter(File(cacheDir,"max.txt")).use {
            it.write((maximumID))
            it.flush()
        }

    }

    private fun reWriteCard() {



    }

    private fun createNewCard() {

    }

    private fun getCurrentTime() : String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
    }

}