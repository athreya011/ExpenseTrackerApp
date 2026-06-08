package com.example.expensetrackerapp.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.expensetrackerapp.model.Category
import com.example.expensetrackerapp.model.Expense
import com.example.expensetrackerapp.model.User
import com.example.expensetrackerapp.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Singleton manager for all SharedPreferences operations.
 * Expenses and categories are stored per user using their username as a key prefix.
 */
class SharedPreferencesManager private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        @Volatile
        private var INSTANCE: SharedPreferencesManager? = null

        fun getInstance(context: Context): SharedPreferencesManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SharedPreferencesManager(context).also { INSTANCE = it }
            }
        }
    }

    // ── Session ──────────────────────────────────────────────────────────────

    fun saveLoggedInUser(user: User) {
        prefs.edit()
            .putString(Constants.KEY_LOGGED_IN_USER, gson.toJson(user))
            .putBoolean(Constants.KEY_IS_LOGGED_IN, true)
            .apply()
    }

    fun getLoggedInUser(): User? {
        val json = prefs.getString(Constants.KEY_LOGGED_IN_USER, null) ?: return null
        return try { gson.fromJson(json, User::class.java) } catch (e: Exception) { null }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false)

    fun clearSession() {
        prefs.edit()
            .remove(Constants.KEY_LOGGED_IN_USER)
            .putBoolean(Constants.KEY_IS_LOGGED_IN, false)
            .apply()
    }

    // ── Users ─────────────────────────────────────────────────────────────────

    fun getUsers(): List<User> = getList(Constants.KEY_USERS_LIST)

    fun saveUsers(users: List<User>) = saveList(Constants.KEY_USERS_LIST, users)

    // ── Per-user Expenses ─────────────────────────────────────────────────────

    /** Returns expenses for the currently logged-in user only. */
    fun getExpenses(): List<Expense> {
        val username = getLoggedInUser()?.username ?: return emptyList()
        return getList("${Constants.KEY_EXPENSES_LIST}_$username")
    }

    /** Saves expenses for the currently logged-in user only. */
    fun saveExpenses(expenses: List<Expense>) {
        val username = getLoggedInUser()?.username ?: return
        saveList("${Constants.KEY_EXPENSES_LIST}_$username", expenses)
    }

    // ── Per-user Categories ───────────────────────────────────────────────────

    /** Returns categories for the currently logged-in user only. */
    fun getCategories(): List<Category> {
        val username = getLoggedInUser()?.username ?: return emptyList()
        return getList("${Constants.KEY_CATEGORIES_LIST}_$username")
    }

    /** Saves categories for the currently logged-in user only. */
    fun saveCategories(categories: List<Category>) {
        val username = getLoggedInUser()?.username ?: return
        saveList("${Constants.KEY_CATEGORIES_LIST}_$username", categories)
    }

    // ── Settings (per user) ───────────────────────────────────────────────────

    fun getCurrencySymbol(): String {
        val username = getLoggedInUser()?.username ?: return "₹"
        return prefs.getString("${Constants.KEY_CURRENCY}_$username", "₹") ?: "₹"
    }

    fun saveCurrencySymbol(symbol: String) {
        val username = getLoggedInUser()?.username ?: return
        prefs.edit().putString("${Constants.KEY_CURRENCY}_$username", symbol).apply()
    }

    fun getDefaultCategory(): String {
        val username = getLoggedInUser()?.username ?: return "Food"
        return prefs.getString("${Constants.KEY_DEFAULT_CATEGORY}_$username", "Food") ?: "Food"
    }

    fun saveDefaultCategory(category: String) {
        val username = getLoggedInUser()?.username ?: return
        prefs.edit().putString("${Constants.KEY_DEFAULT_CATEGORY}_$username", category).apply()
    }

    // ── First-run per user ────────────────────────────────────────────────────

    /** Returns true if this specific user has never logged in before. */
    fun isFirstRunForUser(username: String): Boolean =
        prefs.getBoolean("${Constants.KEY_IS_FIRST_RUN}_$username", true)

    fun setFirstRunDoneForUser(username: String) {
        prefs.edit().putBoolean("${Constants.KEY_IS_FIRST_RUN}_$username", false).apply()
    }

    // ── Generic helpers ───────────────────────────────────────────────────────

    private inline fun <reified T> getList(key: String): List<T> {
        val json = prefs.getString(key, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<T>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) { emptyList() }
    }

    private fun <T> saveList(key: String, list: List<T>) {
        prefs.edit().putString(key, gson.toJson(list)).apply()
    }
}
