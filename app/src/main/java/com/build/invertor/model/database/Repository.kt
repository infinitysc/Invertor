package com.build.invertor.model.database

import android.content.Context
import androidx.room.Room
import com.build.invertor.model.database.card.CardEntity
import com.build.invertor.model.database.card.DAOCard
import com.build.invertor.model.database.data.DAOUser
import com.build.invertor.model.database.data.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class Repository private constructor(private val context : Context) {

    private val database : AppDataBase = Room.databaseBuilder(context,AppDataBase::class.java,"AppDatabase").build()
    private val daoUser = database.getDaoUser()
    private val daoCard = database.getDaoCard()
    suspend fun insertUser(user : UserEntity) {
        withContext(Dispatchers.IO){
            daoUser.insertUser(user)
        }
    }

    suspend fun selectUser(userName : String) {
        withContext(Dispatchers.IO){
            daoUser.selectUser(userName)
        }

    }

    suspend fun getAll() : List<CardEntity> {
        return withContext(Dispatchers.IO) {
           return@withContext daoCard.getAll()
        }
    }

    
    suspend fun insert(cardEntity: CardEntity){
        withContext(Dispatchers.IO){
            daoCard.insertCard(cardEntity)
        }

    }

    suspend fun getListCod1c(code : String) : List<CardEntity> {
        return withContext(Dispatchers.IO){
            return@withContext daoCard.selectListCard(code)
        }
    }

    suspend fun selectFromIndex(index : Int) : CardEntity {
        return withContext(Dispatchers.IO){
            return@withContext daoCard.selectCard(index)
        }

    }

    suspend fun updateCard(cardEntity: CardEntity) {
        withContext(Dispatchers.IO){
            daoCard.updateCard(cardEntity)
        }
    }

    companion object{
        private var INSTANCE : Repository? = null
        fun initialize(context : Context) {

            if(INSTANCE == null){
                INSTANCE = Repository(context)
            }

        }
        fun get() : Repository {
            return INSTANCE ?: throw IllegalStateException("Repository is not initialized")

        }
    }

}