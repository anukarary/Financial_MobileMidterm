package com.example.financial.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val FILTER_KEY = stringPreferencesKey("transaction_filter")
        const val TAG = "UserPreferencesRepo"
    }

    val transactionFilter: Flow<TransactionFilter> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val savedValue = preferences[FILTER_KEY]
            runCatching {
                TransactionFilter.valueOf(savedValue ?: TransactionFilter.ALL.name)
            }.getOrDefault(TransactionFilter.ALL)
        }

    suspend fun saveTransactionFilter(filter: TransactionFilter) {
        dataStore.edit { preferences ->
            preferences[FILTER_KEY] = filter.name
        }
    }
}
