package com.example.financial.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Transaction)

    @Update
    suspend fun update(item: Transaction)

    @Delete
    suspend fun delete(item: Transaction)

    //Сараар нь бүлэглэж гаргах
    @Query("""
    SELECT DISTINCT substr(date, 1, 7)
    FROM transactions
    WHERE (:type IS NULL OR type = :type)
    ORDER BY substr(date, 1, 7) ASC
    """)
    fun observeMonths(type: String?): Flow<List<String>>

    //Тухайн сарын гүйлгээний мэдээллийг гаргах
    @Query("""
    SELECT * FROM transactions
    WHERE substr(date, 1, 7) = :month
      AND (:type IS NULL OR type = :type)
    ORDER BY date DESC, id DESC
    """)
    fun observeTransactionsForMonth(
        month: String,
        type: String?
    ): Flow<List<Transaction>>
}
