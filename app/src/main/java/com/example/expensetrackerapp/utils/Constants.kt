package com.example.expensetrackerapp.utils

/** App-wide constants for SharedPreferences keys and default values. */
object Constants {
    const val PREF_NAME = "expense_tracker_prefs"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    const val KEY_LOGGED_IN_USER = "logged_in_user"
    const val KEY_USERS_LIST = "users_list"
    const val KEY_EXPENSES_LIST = "expenses_list"
    const val KEY_CATEGORIES_LIST = "categories_list"
    const val KEY_CURRENCY = "currency_symbol"
    const val KEY_DEFAULT_CATEGORY = "default_category"
    const val KEY_IS_FIRST_RUN = "is_first_run"
    val DEFAULT_CATEGORIES = listOf("Food", "Travel", "Shopping", "Others")
}
