package com.example.financial.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financial.data.Transaction
import java.time.LocalDate

@Composable
fun FinancialUpdateScreen(
    existing: Transaction?,
    initialType: String,
    onSave: (String, Double, String, String, Transaction?) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now().toString()) }
    var type by remember { mutableStateOf(initialType) }
    var error by remember { mutableStateOf(false) }

    LaunchedEffect(existing, initialType) {
        name = existing?.name.orEmpty()
        amount = existing?.amount?.toString().orEmpty()
        date = existing?.date ?: LocalDate.now().toString()
        type = existing?.type ?: initialType
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp)
                .align(Alignment.TopCenter)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (existing == null) "Гүйлгээ нэмэх" else "Гүйлгээ засах"
            )
            Row {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = type == FinancialViewModel.TYPE_INCOME,
                        onClick = { type = FinancialViewModel.TYPE_INCOME }
                    )
                    Text("Орлого")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = type == FinancialViewModel.TYPE_EXPENSE,
                        onClick = { type = FinancialViewModel.TYPE_EXPENSE }
                    )
                    Text("Зарлага")
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Нэр / тайлбар") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Мөнгөн дүн") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Огноо (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (error) {
                Text("Нэр, эерэг дүн, зөв огноо оруулна уу")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TextButton(
                    onClick = onCancel
                ) {
                    Text("Болих")
                }
                Button(
                    onClick = {
                        val parsedAmount = amount.toDoubleOrNull()
                        val validDate = runCatching {
                            LocalDate.parse(date)
                        }.isSuccess

                        if (name.isBlank() || parsedAmount == null || parsedAmount <= 0.0 || !validDate) {
                            error = true
                        } else {
                            onSave(name, parsedAmount, date, type, existing)
                        }
                    }
                ) {
                    Text("Хадгалах")
                }
            }
        }
    }
}
