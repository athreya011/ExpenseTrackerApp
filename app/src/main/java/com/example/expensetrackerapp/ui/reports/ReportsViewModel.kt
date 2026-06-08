package com.example.expensetrackerapp.ui.reports

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.expensetrackerapp.data.local.SharedPreferencesManager
import com.example.expensetrackerapp.data.repository.ExpenseRepository
import com.example.expensetrackerapp.model.Expense
import com.example.expensetrackerapp.utils.DateUtils

/**
 * ViewModel for ReportsFragment.
 * Provides month navigation and expense breakdowns for charts.
 */
class ReportsViewModel(application: Application) : AndroidViewModel(application) {

    private val expenseRepository = ExpenseRepository(SharedPreferencesManager.getInstance(application))

    private val _selectedMonth = MutableLiveData(DateUtils.getCurrentMonthYear())
    val selectedMonth: LiveData<String> = _selectedMonth

    private val _monthlyExpenses = MutableLiveData<List<Expense>>()
    val monthlyExpenses: LiveData<List<Expense>> = _monthlyExpenses

    private val _categoryBreakdown = MutableLiveData<Map<String, Double>>()
    val categoryBreakdown: LiveData<Map<String, Double>> = _categoryBreakdown

    private val _dailyBreakdown = MutableLiveData<Map<String, Double>>()
    val dailyBreakdown: LiveData<Map<String, Double>> = _dailyBreakdown

    /** Navigates to the previous month and reloads data. */
    fun previousMonth() {
        _selectedMonth.value = DateUtils.addMonths(_selectedMonth.value!!, -1)
        loadReportData()
    }

    /** Navigates to the next month and reloads data. */
    fun nextMonth() {
        _selectedMonth.value = DateUtils.addMonths(_selectedMonth.value!!, 1)
        loadReportData()
    }

    /** Loads expense data for the currently selected month. */
    fun loadReportData() {
        val month = _selectedMonth.value ?: DateUtils.getCurrentMonthYear()
        val expenses = expenseRepository.getExpensesByMonth(month)
        _monthlyExpenses.value = expenses

        // Category breakdown
        _categoryBreakdown.value = expenses
            .groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { it.amount } }

        // Daily breakdown
        _dailyBreakdown.value = expenses
            .groupBy { it.date }
            .mapValues { (_, list) -> list.sumOf { it.amount } }
            .toSortedMap()
    }
}
