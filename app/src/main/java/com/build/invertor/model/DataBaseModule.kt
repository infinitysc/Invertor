package com.build.invertor.model

import android.content.Context
import androidx.room.Room
import com.build.invertor.model.database.AppDataBase
import com.build.invertor.model.database.migration1_2
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DataBaseModule {

    @Singleton
    @Provides
    fun provideDataBase(context : Context) : AppDataBase {
        return Room.databaseBuilder(context,AppDataBase::class.java,"AppDatabase")
            .addMigrations(migration1_2)
            .build()
    }


}