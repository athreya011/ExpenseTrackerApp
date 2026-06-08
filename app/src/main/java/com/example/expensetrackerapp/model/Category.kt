package com.example.expensetrackerapp.model

/** Represents an expense category. */
data class Category(
    val id: String,
    val name: String,
    val isDefault: Boolean = false
)
