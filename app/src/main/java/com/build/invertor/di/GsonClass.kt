package com.build.invertor.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
object GsonClass {

    private val gsonWithoutNulls : Gson = GsonBuilder().create()
    private val gsonWithNUlls : Gson = GsonBuilder().serializeNulls().create()


    @Provides
    @Singleton
    fun getGson() : Gson {
        return this.gsonWithoutNulls
    }


    @Provides
    @Singleton
    @Named("GsonSerNulls")
    fun gsonWithNulls() : Gson {
        return this.gsonWithNUlls
    }

}