package com.example.expensetrackerapp.data.repository

import com.example.expensetrackerapp.data.local.SharedPreferencesManager
import com.example.expensetrackerapp.model.Expense

/**
 * Repository for CRUD operations on Expense data stored in SharedPreferences.
 */
class ExpenseRepository(private val prefs: SharedPreferencesManager) {

    /** Returns all expenses sorted by timestamp descending. */
    fun getExpenses(): List<Expense> =
        prefs.getExpenses().sortedByDescending { it.timestamp }

    /** Adds a new expense or updates an existing one (matched by id). */
    fun saveExpense(expense: Expense) {
        val list = prefs.getExpenses().toMutableList()
        val idx = list.indexOfFirst { it.id == expense.id }
        if (idx >= 0) list[idx] = expense else list.add(expense)
        prefs.saveExpenses(list)
    }

    /** Removes the expense with the given id. */
    fun deleteExpense(expenseId: String) {
        val list = prefs.getExpenses().toMutableList()
        list.removeAll { it.id == expenseId }
        prefs.saveExpenses(list)
    }

    /** Returns a single expense by id, or null. */
    fun getExpense(id: String): Expense? = prefs.getExpenses().find { it.id == id }

    /** Returns expenses filtered by category name. */
    fun getExpensesByCategory(category: String): List<Expense> =
        getExpenses().filter { it.category == category }

    /** Returns expenses whose date starts with the given yyyy-MM month string. */
    fun getExpensesByMonth(monthYear: String): List<Expense> =
        getExpenses().filter { it.date.startsWith(monthYear) }
}
