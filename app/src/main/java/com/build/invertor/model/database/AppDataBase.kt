package com.build.invertor.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.build.invertor.model.database.card.CardEntity
import com.build.invertor.model.database.card.DAOCard
import com.build.invertor.model.database.data.DAOUser
import com.build.invertor.model.database.data.UserEntity


@Database(version = 1, entities = [CardEntity::class, UserEntity::class])
abstract class AppDataBase : RoomDatabase(){

abstract fun getDaoCard() : DAOCard
abstract fun getDaoUser() : DAOUser


}