package com.example.expensetrackerapp.model

/** Represents an app user stored in SharedPreferences. */
data class User(
    val username: String,
    val email: String,
    val password: String
)
