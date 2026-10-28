package com.example.financial.data

import kotlinx.coroutines.flow.Flow

class OfflineTransactionRepository(private val transactionDao: TransactionDao) : TransactionRepository {
    override suspend fun insert(item: Transaction) = transactionDao.insert(item)
    override suspend fun update(item: Transaction) = transactionDao.update(item)
    override suspend fun delete(item: Transaction) = transactionDao.delete(item)
    override fun observeMonths(type: String?): Flow<List<String>> = transactionDao.observeMonths(type)
    override fun observeTransactionsForMonth(
        month: String,
        type: String?
    ): Flow<List<Transaction>> = transactionDao.observeTransactionsForMonth(month, type)
}
