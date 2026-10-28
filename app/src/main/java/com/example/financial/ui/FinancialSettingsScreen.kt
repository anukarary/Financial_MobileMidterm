package com.example.financial.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financial.data.TransactionFilter

@Composable
fun FinancialSettingsScreen(
    selectedFilter: TransactionFilter,
    onFilterSelected: (TransactionFilter) -> Unit
) {
    val filterOptions = listOf(
        TransactionFilter.ALL,
        TransactionFilter.INCOME,
        TransactionFilter.EXPENSE
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .align(Alignment.TopCenter)
                .padding(20.dp)
        ) {
            Text("Жагсаалтад харуулах гүйлгээ")

            filterOptions.forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedFilter == option,
                        onClick = { onFilterSelected(option) }
                    )
                    Text(option.displayName())
                }
            }
        }
    }
}

private fun TransactionFilter.displayName(): String {
    return when (this) {
        TransactionFilter.ALL -> "Бүгд"
        TransactionFilter.INCOME -> "Орлого"
        TransactionFilter.EXPENSE -> "Зарлага"
    }
}
