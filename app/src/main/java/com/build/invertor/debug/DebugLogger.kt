package com.build.invertor.debug

import java.io.File

class DebugLogger : DebugSharedInterface {

    private val container : MutableList<String> = mutableListOf()

    private val fileLogName = "loggerFile.txt"

    override fun getLoggerContainer(): List<String> {
        return this.container
    }

    override fun putMessage(message : String ) {
        this.container.add(message)
    }

    override fun clearContainer() {
        this.container.clear()
    }

    private fun logFileIsCreated(fileDir : File) : Boolean{
        val file = File(fileDir,fileLogName)
        if(!file.exists()){
            return false
        }
        return true
    }

}