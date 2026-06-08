package com.example.expensetrackerapp.ui.expense

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.expensetrackerapp.data.local.SharedPreferencesManager
import com.example.expensetrackerapp.data.repository.CategoryRepository
import com.example.expensetrackerapp.data.repository.ExpenseRepository
import com.example.expensetrackerapp.model.Expense

/** Sort order options for expense history. */
enum class SortOrder { DATE_DESC, DATE_ASC, AMOUNT_DESC }

/**
 * ViewModel for ExpenseHistoryFragment.
 * Supports filtering by category and sorting.
 */
class ExpenseHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = SharedPreferencesManager.getInstance(application)
    private val expenseRepository = ExpenseRepository(prefs)
    private val categoryRepository = CategoryRepository(prefs)

    private val _filteredExpenses = MutableLiveData<List<Expense>>()
    val filteredExpenses: LiveData<List<Expense>> = _filteredExpenses

    private val _categoryNames = MutableLiveData<List<String>>()
    val categoryNames: LiveData<List<String>> = _categoryNames

    private var currentFilter: String = "All"
    private var currentSort: SortOrder = SortOrder.DATE_DESC

    /** Loads categories and applies current filter/sort. */
    fun loadData() {
        _categoryNames.value = listOf("All") + categoryRepository.getCategoryNames()
        applyFilterAndSort()
    }

    /** Sets the category filter and refreshes the list. */
    fun setFilter(category: String) {
        currentFilter = category
        applyFilterAndSort()
    }

    /** Sets the sort order and refreshes the list. */
    fun setSortOrder(order: SortOrder) {
        currentSort = order
        applyFilterAndSort()
    }

    private fun applyFilterAndSort() {
        var expenses = expenseRepository.getExpenses()

        if (currentFilter != "All") {
            expenses = expenses.filter { it.category == currentFilter }
        }

        expenses = when (currentSort) {
            SortOrder.DATE_DESC -> expenses.sortedByDescending { it.date }
            SortOrder.DATE_ASC -> expenses.sortedBy { it.date }
            SortOrder.AMOUNT_DESC -> expenses.sortedByDescending { it.amount }
        }

        _filteredExpenses.value = expenses
    }
}
