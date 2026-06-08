package com.example.expensetrackerapp.utils

import android.util.Patterns

/** Utility functions for input validation. */
object ValidationUtils {

    /** Returns true if the string is not null and not blank. */
    fun isNotEmpty(value: String?): Boolean = !value.isNullOrBlank()

    /** Returns true if the email matches a valid email pattern. */
    fun isValidEmail(email: String): Boolean =
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    /** Returns true if the password is at least 6 characters. */
    fun isValidPassword(password: String): Boolean = password.length >= 6

    /** Returns true if the amount string is a positive number. */
    fun isValidAmount(amount: String): Boolean =
        amount.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0
}
