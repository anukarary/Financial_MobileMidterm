package com.example.financial.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.financial.FinancialApplication
import com.example.financial.data.Transaction
import com.example.financial.data.TransactionFilter
import com.example.financial.data.TransactionRepository
import com.example.financial.data.UserPreferencesRepository
import java.time.YearMonth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FinancialUiState(
    val transactions: List<Transaction> = emptyList(),
    val incomeTotal: Double = 0.0,
    val expenseTotal: Double = 0.0,
    val hasTransactions: Boolean = false,
    val currentMonth: YearMonth = YearMonth.now(),
    val filter: TransactionFilter = TransactionFilter.ALL,
    val canGoNextMonth: Boolean = false
)

class FinancialViewModel(
    private val repository: TransactionRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val selectedMonth = MutableStateFlow(YearMonth.now())

    private val filter: StateFlow<TransactionFilter> = preferencesRepository.transactionFilter
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            TransactionFilter.ALL
        )

    private val hasTransactions: StateFlow<Boolean> = repository
        .observeMonths(null)
        .map { months -> months.isNotEmpty() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            false
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val monthTransactions: StateFlow<List<Transaction>> = selectedMonth
        .flatMapLatest { repository.observeTransactionsForMonth(it.toString(), null) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val uiState: StateFlow<FinancialUiState> = combine(
        monthTransactions,
        filter,
        hasTransactions,
        selectedMonth
    ) { items, selectedFilter, hasAnyTransactions, month ->
        val visibleTransactions = when (selectedFilter) {
            TransactionFilter.INCOME -> items.filter { it.type == TYPE_INCOME }
            TransactionFilter.EXPENSE -> items.filter { it.type == TYPE_EXPENSE }
            TransactionFilter.ALL -> items
        }

        FinancialUiState(
            transactions = visibleTransactions,
            incomeTotal = items
                .filter { it.type == TYPE_INCOME }
                .sumOf { it.amount },
            expenseTotal = items
                .filter { it.type == TYPE_EXPENSE }
                .sumOf { it.amount },
            hasTransactions = hasAnyTransactions,
            currentMonth = month,
            filter = selectedFilter,
            canGoNextMonth = hasAnyTransactions && month.isBefore(YearMonth.now())
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FinancialUiState()
    )

    fun previousMonth() {
        selectedMonth.value = selectedMonth.value.minusMonths(1)
    }

    fun nextMonth() {
        if (selectedMonth.value.isBefore(YearMonth.now())) {
            selectedMonth.value = selectedMonth.value.plusMonths(1)
        }
    }

    fun selectFilter(filter: TransactionFilter) {
        viewModelScope.launch {
            preferencesRepository.saveTransactionFilter(filter)
        }
    }

    fun save(name: String, amount: Double, date: String, type: String, existing: Transaction?) {
        viewModelScope.launch {
            val transaction = Transaction(
                id = existing?.id ?: 0,
                type = type,
                name = name.trim(),
                amount = amount,
                date = date
            )
            if (existing == null) {
                repository.insert(transaction)
            } else {
                repository.update(transaction)
            }
        }
    }

    fun delete(transaction: Transaction) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }

    fun monthTitle(month: YearMonth): String {
        return "${month.year} оны ${month.monthValue} сар"
    }

    companion object {
        const val TYPE_INCOME = "income"
        const val TYPE_EXPENSE = "expense"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as FinancialApplication

                FinancialViewModel(
                    repository = application.container.transactionRepository,
                    preferencesRepository = application.container.userPreferencesRepository
                )
            }
        }
    }
}


