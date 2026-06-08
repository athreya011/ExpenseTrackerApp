package com.example.expensetrackerapp.ui.expense

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.expensetrackerapp.data.local.SharedPreferencesManager
import com.example.expensetrackerapp.data.repository.CategoryRepository
import com.example.expensetrackerapp.data.repository.ExpenseRepository
import com.example.expensetrackerapp.model.Category
import com.example.expensetrackerapp.model.Expense
import com.example.expensetrackerapp.utils.ValidationUtils

/**
 * ViewModel for AddExpenseFragment.
 * Handles loading categories, saving/updating/deleting expenses, and validation.
 */
class AddExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = SharedPreferencesManager.getInstance(application)
    private val expenseRepository = ExpenseRepository(prefs)
    private val categoryRepository = CategoryRepository(prefs)

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _validationError = MutableLiveData<String>()
    val validationError: LiveData<String> = _validationError

    private val _operationSuccess = MutableLiveData<Boolean>()
    val operationSuccess: LiveData<Boolean> = _operationSuccess

    /** Loads all categories from repository. */
    fun loadCategories() {
        _categories.value = categoryRepository.getCategories()
    }

    /** Returns a single expense by id, or null. */
    fun getExpense(id: String): Expense? = expenseRepository.getExpense(id)

    /** Validates and saves a new expense. */
    fun saveExpense(expense: Expense) {
        if (!validate(expense)) return
        expenseRepository.saveExpense(expense)
        _operationSuccess.value = true
    }

    /** Validates and updates an existing expense. */
    fun updateExpense(expense: Expense) {
        if (!validate(expense)) return
        expenseRepository.saveExpense(expense)
        _operationSuccess.value = true
    }

    /** Deletes an expense by id. */
    fun deleteExpense(expenseId: String) {
        expenseRepository.deleteExpense(expenseId)
        _operationSuccess.value = true
    }

    private fun validate(expense: Expense): Boolean {
        return when {
            !ValidationUtils.isValidAmount(expense.amount.toString()) -> {
                _validationError.value = "Amount must be greater than 0"
                false
            }
            !ValidationUtils.isNotEmpty(expense.category) -> {
                _validationError.value = "Please select a category"
                false
            }
            !ValidationUtils.isNotEmpty(expense.date) -> {
                _validationError.value = "Please select a date"
                false
            }
            else -> true
        }
    }
}
