package com.build.invertor.model.database

import android.content.Context
import androidx.room.Room
import com.build.invertor.model.database.card.CardEntity
import com.build.invertor.model.database.card.Codes
import com.build.invertor.model.database.card.DAOCard
import com.build.invertor.model.database.data.DAOUser
import com.build.invertor.model.database.data.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

annotation class User()

annotation class Card()


@Singleton
class Repository @Inject constructor(private val database : AppDataBase) {

    private val daoUser = database.getDaoUser()
    private val daoCard = database.getDaoCard()
    suspend fun insertUser(user : UserEntity) {
        withContext(Dispatchers.IO){
            daoUser.insertUser(user)
        }
    }

    @User
    suspend fun getUserNames() : List<String> {
        return withContext(Dispatchers.IO){
            return@withContext daoUser.getAllNames()
        }
    }


    @User
    suspend fun getAllUsers() : List<UserEntity> {
        return withContext(Dispatchers.IO) {
            daoUser.getAllUsers()
        }
    }
    @User
    suspend fun selectUser(userName : String) : UserEntity? {
       return withContext(Dispatchers.IO){
            daoUser.getUserEntityByUserName(userName)
        }

    }

    @Card
    suspend fun getAll() : List<CardEntity> {
        return withContext(Dispatchers.IO) {
           return@withContext daoCard.getAll()
        }
    }

    @Card
    suspend fun insert(cardEntity: CardEntity){
        withContext(Dispatchers.IO){
            daoCard.insertCard(cardEntity)
        }

    }

    @Card
    suspend fun getListCode() : List<Codes>{
        return withContext(Dispatchers.IO) {
            return@withContext daoCard.selectCodes()
        }
    }

    @Card
    suspend fun selectByString(str : String) : List<CardEntity> {
        return withContext(Dispatchers.IO) {
            return@withContext daoCard.selectCardByString(str)
        }
    }

    @Card
    suspend fun getListCod1c(code : String) : List<CardEntity> {
        return withContext(Dispatchers.IO){
            return@withContext daoCard.selectListCardUseCode1C(code)
        }
    }

    @Card
    suspend fun getListInventoryNumber(inventoryNumber : String) : List<CardEntity> {
        return withContext(Dispatchers.IO){
            return@withContext daoCard.selectListCardUseInventoryNumber(inventoryNumber)
        }
    }

    @Card
    suspend fun selectFromIndex(index : Int) : CardEntity {
        return withContext(Dispatchers.IO){
            return@withContext daoCard.selectCard(index)
        }

    }

    @Card
    suspend fun updateCard(cardEntity: CardEntity) {
        withContext(Dispatchers.IO){
            daoCard.updateCard(cardEntity)
        }
    }

}