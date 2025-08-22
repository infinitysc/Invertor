package com.build.invertor.model.database

import android.content.Context
import com.google.android.datatransport.runtime.dagger.Provides
import dagger.Module
import javax.inject.Singleton

@Module
class DatabaseProvider {

    @Provides
    @Singleton
    fun createRepository(context : Context) : Repository{
        return Repository(context)
    }

}