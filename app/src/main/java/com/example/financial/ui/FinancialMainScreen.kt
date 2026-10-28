package com.example.financial.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.example.financial.data.Transaction
import com.example.financial.ui.theme.ExpenseAmountColor
import com.example.financial.ui.theme.IncomeAmountColor
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
// Үндсэн дэлгэцийн сарын дүн, гүйлгээний жагсаалт болон товчлууруудыг харуулна.
fun FinancialMainScreen(
    monthLabel: String,
    incomeTotal: Double,
    expenseTotal: Double,
    transactions: List<Transaction>,
    windowSize: WindowWidthSizeClass,
    hasTransactions: Boolean,
    canGoPreviousMonth: Boolean,
    canGoNextMonth: Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSettings: () -> Unit,
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit,
    onEditTransaction: (Transaction) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit
) {
    var amountsVisible by rememberSaveable { mutableStateOf(false) }
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }
    val isExpanded = windowSize != WindowWidthSizeClass.Compact

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Санхүү") },
                    actions = {
                        IconButton(
                            onClick = onSettings,
                            enabled = hasTransactions
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Тохиргоо"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                if (!isExpanded) {
                    AddTransactionButtons(
                        onAddIncome = onAddIncome,
                        onAddExpense = onAddExpense
                    )
                }
            }
        ) { innerPadding ->
            if (isExpanded) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MonthSelector(
                            monthLabel = monthLabel,
                            canGoPreviousMonth = canGoPreviousMonth,
                            canGoNextMonth = canGoNextMonth,
                            onPreviousMonth = onPreviousMonth,
                            onNextMonth = onNextMonth
                        )
                        SummaryCardsRow(
                            incomeTotal = incomeTotal,
                            expenseTotal = expenseTotal,
                            amountsVisible = amountsVisible
                        )

                        Spacer(Modifier.height(8.dp))
                        AddTransactionButtons(
                            onAddIncome = onAddIncome,
                            onAddExpense = onAddExpense
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        TransactionsHeader(
                            amountsVisible = amountsVisible,
                            hasTransactions = hasTransactions,
                            onToggleAmounts = {
                                amountsVisible = !amountsVisible
                            }
                        )
                        Spacer(Modifier.height(8.dp))
                        TransactionList(
                            transactions = transactions,
                            amountsVisible = amountsVisible,
                            onToggleAmounts = { amountsVisible = !amountsVisible },
                            onEditTransaction = onEditTransaction,
                            onRequestDelete = { transactionToDelete = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        MonthSelector(
                            monthLabel = monthLabel,
                            canGoPreviousMonth = canGoPreviousMonth,
                            canGoNextMonth = canGoNextMonth,
                            onPreviousMonth = onPreviousMonth,
                            onNextMonth = onNextMonth
                        )
                    }

                    item {
                        SummaryCardsRow(
                            incomeTotal = incomeTotal,
                            expenseTotal = expenseTotal,
                            amountsVisible = amountsVisible
                        )
                    }

                    item {
                        TransactionsHeader(
                            amountsVisible = amountsVisible,
                            hasTransactions = hasTransactions,
                            onToggleAmounts = {
                                amountsVisible = !amountsVisible
                            }
                        )
                    }

                    if (transactions.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Энэ сард бүртгэл алга")
                            }
                        }
                    } else {
                        items(
                            items = transactions,
                            key = { transaction -> transaction.id }
                        ) { transaction ->
                            TransactionCard(
                                transaction = transaction,
                                amountsVisible = amountsVisible,
                                onToggleAmounts = { amountsVisible = !amountsVisible },
                                onEditTransaction = onEditTransaction,
                                onRequestDelete = { transactionToDelete = it }
                            )
                        }
                    }
                }
            }
        }
    }

    transactionToDelete?.let { transaction ->
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Бүртгэл устгах уу?") },
            text = { Text("${transaction.name} бүртгэлийг устгах уу?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTransaction(transaction)
                        transactionToDelete = null
                    }
                ) {
                    Text("Устгах")
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text("Болих")
                }
            }
        )
    }
}

@Composable
// Орлого болон зарлага нэмэх товчлууруудыг харуулна.
private fun AddTransactionButtons(
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onAddIncome,
            modifier = Modifier.weight(1f)
        ) {
            Text("Орлого нэмэх")
        }

        Button(
            onClick = onAddExpense,
            modifier = Modifier.weight(1f)
        ) {
            Text("Зарлага нэмэх")
        }
    }
}

@Composable
// Өмнөх болон дараагийн сарын товчлууруудтай сарын нэрийг харуулна.
private fun MonthSelector(
    monthLabel: String,
    canGoPreviousMonth: Boolean,
    canGoNextMonth: Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(
            onClick = onPreviousMonth,
            enabled = canGoPreviousMonth
        ) {
            Text("<")
        }

        Text(
            text = monthLabel,
            style = MaterialTheme.typography.titleLarge
        )

        TextButton(
            onClick = onNextMonth,
            enabled = canGoNextMonth
        ) {
            Text(">")
        }
    }
}

@Composable
private fun TransactionsHeader(
    amountsVisible: Boolean,
    hasTransactions: Boolean,
    onToggleAmounts: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Гүйлгээнүүд",
            style = MaterialTheme.typography.titleLarge
        )

        TextButton(
            onClick = onToggleAmounts,
            enabled = hasTransactions
        ) {
            Text(if (amountsVisible) "Дүн нуух" else "Дүн харуулах")
        }
    }
}

@Composable
private fun SummaryCardsRow(
    incomeTotal: Double,
    expenseTotal: Double,
    amountsVisible: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            title = "Нийт орлого",
            amount = incomeTotal,
            visible = amountsVisible,
            amountColor = IncomeAmountColor,
            modifier = Modifier.weight(1f)
        )

        SummaryCard(
            title = "Нийт зарлага",
            amount = expenseTotal,
            visible = amountsVisible,
            amountColor = ExpenseAmountColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
// Сонгосон сарын гүйлгээнүүдийг гүйлгэж үзэх жагсаалт
private fun TransactionList(
    transactions: List<Transaction>,
    amountsVisible: Boolean,
    onToggleAmounts: () -> Unit,
    onEditTransaction: (Transaction) -> Unit,
    onRequestDelete: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    if (transactions.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text("Энэ сард бүртгэл алга")
        }
    } else {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = transactions,
                key = { transaction -> transaction.id }
            ) { transaction ->
                TransactionCard(
                    transaction = transaction,
                    amountsVisible = amountsVisible,
                    onToggleAmounts = onToggleAmounts,
                    onEditTransaction = onEditTransaction,
                    onRequestDelete = onRequestDelete
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
// Нэг гүйлгээний мэдээлэл, дүн, устгах үйлдлийг харуулна.
private fun TransactionCard(
    transaction: Transaction,
    amountsVisible: Boolean,
    onToggleAmounts: () -> Unit,
    onEditTransaction: (Transaction) -> Unit,
    onRequestDelete: (Transaction) -> Unit
) {
    val isIncome = transaction.type == "income"
    val amountColor = if (isIncome) {
        IncomeAmountColor
    } else {
        ExpenseAmountColor
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    onEditTransaction(transaction)
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = transaction.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(if (isIncome) "Орлого" else "Зарлага")
                Text(transaction.date)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (amountsVisible) {
                        "${formatAmount(transaction.amount)} ₮"
                    } else {
                        "**** ₮"
                    },
                    modifier = Modifier.clickable(onClick = onToggleAmounts),
                    color = amountColor,
                    style = MaterialTheme.typography.titleMedium
                )

                TextButton(
                    onClick = {
                        onRequestDelete(transaction)
                    }
                ) {
                    Text("Устгах")
                }
            }
        }
    }
}


@Composable
private fun SummaryCard(
    title: String,
    amount: Double,
    visible: Boolean,
    amountColor: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(Modifier.padding(12.dp)) {
            Text(title)
            Text(
                text = if (visible) "${formatAmount(amount)} ₮" else "**** ₮",
                color = amountColor,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

private fun formatAmount(amount: Double): String =
    NumberFormat.getNumberInstance(Locale.forLanguageTag("mn-MN")).format(amount)
