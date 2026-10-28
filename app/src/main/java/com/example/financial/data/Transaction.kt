package com.example.financial.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String,
    val name: String,
    val amount: Double,
    val date: String
)