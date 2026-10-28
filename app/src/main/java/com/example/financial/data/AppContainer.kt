package com.example.financial.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "financial_settings"
)

interface AppContainer {
    val transactionRepository: TransactionRepository
    val userPreferencesRepository: UserPreferencesRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.settingsDataStore)
    }

    override val transactionRepository: TransactionRepository by lazy {
        OfflineTransactionRepository(FinancialDatabase.getDatabase(context).transactionDao())
    }
}
