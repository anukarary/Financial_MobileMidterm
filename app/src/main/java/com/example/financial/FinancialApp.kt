package com.example.financial

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.financial.data.Transaction
import com.example.financial.ui.FinancialMainScreen
import com.example.financial.ui.FinancialSettingsScreen
import com.example.financial.ui.FinancialUpdateScreen
import com.example.financial.ui.FinancialViewModel

enum class FinancialScreen(@param:StringRes val title: Int) {
    Home(R.string.app_name),
    Update(R.string.app_update),
    Settings(R.string.app_settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialAppBar(
    @StringRes currentScreenTitle: Int,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Text(stringResource(currentScreenTitle))
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun FinancialApp(
    windowSize: WindowWidthSizeClass,
    navController: NavHostController = rememberNavController()
) {
    val vm: FinancialViewModel = viewModel(factory = FinancialViewModel.Factory)
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: FinancialScreen.Home.name
    val screen = FinancialScreen.valueOf(currentRoute)

    val uiState by vm.uiState.collectAsStateWithLifecycle()

    var editing by remember { mutableStateOf<Transaction?>(null) }
    var addType by remember { mutableStateOf(FinancialViewModel.TYPE_INCOME) }

    Scaffold(
        topBar = {
            if (screen != FinancialScreen.Home) {
                FinancialAppBar(
                    currentScreenTitle = screen.title,
                    canNavigateBack = true,
                    navigateUp = { navController.navigateUp() }
                )
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            NavHost(
                navController = navController,
                startDestination = FinancialScreen.Home.name
            ) {
                composable(FinancialScreen.Home.name) {
                    FinancialMainScreen(
                        monthLabel = vm.monthTitle(uiState.currentMonth),
                        incomeTotal = uiState.incomeTotal,
                        expenseTotal = uiState.expenseTotal,
                        transactions = uiState.transactions,
                        windowSize = windowSize,
                        hasTransactions = uiState.hasTransactions,
                        canGoPreviousMonth = uiState.hasTransactions,
                        canGoNextMonth = uiState.canGoNextMonth,
                        onPreviousMonth = vm::previousMonth,
                        onNextMonth = vm::nextMonth,
                        onSettings = { navController.navigate(FinancialScreen.Settings.name) },
                        onAddIncome = {
                            editing = null
                            addType = FinancialViewModel.TYPE_INCOME
                            navController.navigate(FinancialScreen.Update.name)
                        },
                        onAddExpense = {
                            editing = null
                            addType = FinancialViewModel.TYPE_EXPENSE
                            navController.navigate(FinancialScreen.Update.name)
                        },
                        onEditTransaction = { transaction ->
                            editing = transaction
                            addType = transaction.type
                            navController.navigate(FinancialScreen.Update.name)
                        },
                        onDeleteTransaction = vm::delete
                    )
                }
                composable(FinancialScreen.Update.name) {
                    FinancialUpdateScreen(
                        existing = editing,
                        initialType = addType,
                        onSave = { name, amount, date, type, old ->
                            vm.save(name, amount, date, type, old)
                            editing = null
                            navController.popBackStack()
                        },
                        onCancel = {
                            editing = null
                            navController.popBackStack()
                        }
                    )
                }
                composable(FinancialScreen.Settings.name) {
                    FinancialSettingsScreen(uiState.filter, vm::selectFilter)
                }
            }
        }
    }
}
