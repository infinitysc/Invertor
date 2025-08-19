package com.build.invertor.model.database

import android.app.Application
import android.content.Context
import androidx.room.Room

object Dependencies {

    private lateinit var appContext : Context

    fun init(context: Context){
        appContext = context

    }

    private val appDatabase : AppDataBase by lazy {
        Room.databaseBuilder(appContext , AppDataBase::class.java,"database.db").build()
    }

    fun getDatabase() = appDatabase
}