package com.example.financial.data

import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun insert(item: Transaction)
    suspend fun update(item: Transaction)
    suspend fun delete(item: Transaction)
    fun observeMonths(type: String?): Flow<List<String>>
    fun observeTransactionsForMonth(
        month: String,
        type: String?
    ): Flow<List<Transaction>>
}