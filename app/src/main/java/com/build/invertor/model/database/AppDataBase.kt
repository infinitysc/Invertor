package com.build.invertor.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.build.invertor.model.database.card.CardEntity
import com.build.invertor.model.database.card.DAOCard
import com.build.invertor.model.database.data.DAOUser
import com.build.invertor.model.database.data.UserEntity


@Database(version = 2,
    entities = [CardEntity::class, UserEntity::class],
    exportSchema = true)
abstract class AppDataBase : RoomDatabase(){
    abstract fun getDaoCard() : DAOCard
    abstract fun getDaoUser() : DAOUser
}




val migration1_2 = object : Migration(1,2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE card_temp (
                id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                SID INTEGER NOT NULL,
                UEID INTEGER,
                UEDescription TEXT,
                ActionDateTime TEXT,
                Adress TEXT,
                Status TEXT,
                InventNumb TEXT,
                SerialNumb TEXT,
                IS_SN_EDITED INTEGER NOT NULL,
                UserName TEXT,
                Description TEXT,
                Cabinet TEXT,
                Code1C TEXT,
                ParentEqueipment INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO card_temp (
                SID, UEID, UEDescription, ActionDateTime, Adress, Status, 
                InventNumb, SerialNumb, IS_SN_EDITED, UserName, Description, 
                Cabinet, Code1C, ParentEqueipment
            )
            SELECT 
                SID, UEID, UEDescription, ActionDateTime, Adress, Status, 
                InventNumb, SerialNumb, IS_SN_EDITED, UserName, Description, 
                Cabinet, Code1C, ParentEqueipment
            FROM Card
            """.trimIndent()
        )

        db.execSQL("DROP TABLE Card")

        db.execSQL("ALTER TABLE card_temp RENAME TO Card")
    }
}