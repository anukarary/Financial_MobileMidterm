package com.example.financial.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver
import kotlinx.coroutines.Dispatchers

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
abstract class FinancialDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var Instance: FinancialDatabase? = null

        fun getDatabase(context: Context): FinancialDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder<FinancialDatabase>(
                    context.applicationContext,
                    "financial_database"
                )
                    .setDriver(AndroidSQLiteDriver())
                    .setQueryCoroutineContext(Dispatchers.IO)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
