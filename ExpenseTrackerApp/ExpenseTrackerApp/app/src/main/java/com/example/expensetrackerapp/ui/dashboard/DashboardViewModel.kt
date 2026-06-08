package com.example.expensetrackerapp.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.expensetrackerapp.data.local.SharedPreferencesManager
import com.example.expensetrackerapp.data.repository.AuthRepository
import com.example.expensetrackerapp.data.repository.ExpenseRepository
import com.example.expensetrackerapp.model.Expense
import com.example.expensetrackerapp.utils.CurrencyUtils

/**
 * ViewModel for DashboardFragment.
 * Exposes greeting, totals, category summary, recent transactions, and currency symbol.
 * All monetary values are raw INR — the fragment/adapter applies conversion via CurrencyUtils.
 */
class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = SharedPreferencesManager.getInstance(application)
    private val expenseRepository = ExpenseRepository(prefs)
    private val authRepository = AuthRepository(prefs)

    private val _greeting = MutableLiveData<String>()
    val greeting: LiveData<String> = _greeting

    private val _totalExpenses = MutableLiveData<Double>()
    val totalExpenses: LiveData<Double> = _totalExpenses

    private val _categoryWiseSummary = MutableLiveData<Map<String, Double>>()
    val categoryWiseSummary: LiveData<Map<String, Double>> = _categoryWiseSummary

    private val _recentTransactions = MutableLiveData<List<Expense>>()
    val recentTransactions: LiveData<List<Expense>> = _recentTransactions

    private val _currencySymbol = MutableLiveData<String>()
    val currencySymbol: LiveData<String> = _currencySymbol

    /** Loads all dashboard data and the current currency preference. */
    fun loadDashboardData() {
        val user = authRepository.getLoggedInUser()
        _greeting.value = "Hello, ${user?.username ?: "User"}!"

        val currency = prefs.getCurrencySymbol()
        _currencySymbol.value = currency

        val expenses = expenseRepository.getExpenses()
        _totalExpenses.value = expenses.sumOf { it.amount }

        val summary = expenses.groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { it.amount } }
        _categoryWiseSummary.value = summary

        _recentTransactions.value = expenses.take(5)
    }
}
