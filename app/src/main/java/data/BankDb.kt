package com.example.proyecto.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import data.entities.Transaction
import data.entities.Rule
import data.entities.Movement
import data.BankDao

@Database(entities = [Transaction::class, Rule::class, Movement::class], version = 7, exportSchema = false)
abstract class BankDb : RoomDatabase() {
    abstract fun bankDao(): BankDao

    companion object {
        @Volatile private var INSTANCE: BankDb? = null

        fun getInstance(context: Context): BankDb =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    BankDb::class.java,
                    "bank.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
