package com.build.invertor.model.database.card

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface DAOCard {

    @Query("SELECT * FROM Card WHERE Code1C = :cod1c")
    suspend fun selectListCardUseCode1C(cod1c : String) : List<CardEntity>

    @Query(value = "SELECT * FROM Card WHERE InventNumb = :inventoryNumber")
    suspend fun selectListCardUseInventoryNumber(inventoryNumber : String) : List<CardEntity>

    @Query("SELECT * FROM Card WHERE id =:index_")
    suspend fun selectCard(index_ : Int) : CardEntity

    @Query("SELECT * FROM Card")
    suspend fun getAll() : List<CardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(cardEntity : CardEntity)

    @Update
    suspend fun updateCard(cardEntity: CardEntity)

    @Query("SELECT * FROM Card WHERE InventNumb = :string OR Code1C = :string")
    suspend fun selectCardByString(string : String) : List<CardEntity>

    @Query("SELECT InventNumb , Code1C FROM Card")
    suspend fun selectCodes() : List<Codes>
}