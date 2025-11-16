package com.build.invertor.di

import android.content.Context
import dagger.Module
import dagger.Provides
import java.io.File
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Module
object FileCreator {

    @Provides
    @Named("FileDir")
    fun createFileDir(context : Context) : File {
        return context.filesDir
    }

    @Provides
    @Named("CacheDir")
    fun cacheFileDir(context : Context) : File {
        return context.cacheDir
    }


    @Provides
    @Named("Json")
    fun createInputStream(context : Context) : InputStream{
        val fileName = "jso.json"
        return context.openFileInput(fileName)
    }

    @Provides
    @Named("Excel")
    fun createInputStreamExcel(context : Context) : InputStream {
        val fileName = "data.xlsx"
        return context.openFileInput(fileName)
    }


}