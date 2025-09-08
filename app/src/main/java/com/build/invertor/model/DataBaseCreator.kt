package com.build.invertor.model

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.build.invertor.model.database.AppDataBase
import com.build.invertor.model.database.migration1_2
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DataBaseCreator {

    @Singleton
    @Provides
    fun provideDataBase(context : Context) : AppDataBase {
        return Room.databaseBuilder(context,AppDataBase::class.java,"AppDatabase")
            .addMigrations(migration1_2)
            .build()
    }


}