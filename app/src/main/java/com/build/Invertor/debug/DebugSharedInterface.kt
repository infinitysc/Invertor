package com.build.Invertor.debug

interface DebugSharedInterface {
    fun putMessage(message : String)
    fun getLoggerContainer() : List<String>
    fun clearContainer()
}