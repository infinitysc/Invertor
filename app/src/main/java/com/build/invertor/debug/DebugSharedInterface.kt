package com.build.invertor.debug

interface DebugSharedInterface {
    fun putMessage(message : String)
    fun getLoggerContainer() : List<String>
    fun clearContainer()
}