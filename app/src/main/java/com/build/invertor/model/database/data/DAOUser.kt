package com.build.invertor.model.database.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DAOUser {

    @Query("SELECT * FROM UserCard WHERE user = :user")
    suspend fun selectUser(user : String) : UserEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user : UserEntity)


}
