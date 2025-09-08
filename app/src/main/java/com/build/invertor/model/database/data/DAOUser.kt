package com.build.invertor.model.database.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DAOUser {

    @Query("SELECT * FROM UserCard WHERE user = :user")
    suspend fun getUserEntityByUserName(user : String) : UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user : UserEntity)


    @Query("SELECT * FROM UserCard")
    suspend fun getAllUsers() : List<UserEntity>



    @Query("SELECT UserCard.user FROM UserCard")
    suspend fun getAllNames() : List<String>
}
