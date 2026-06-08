package com.example.expensetrackerapp.model

/** Represents a single expense entry. */
data class Expense(
    val id: String,           // UUID
    val amount: Double,
    val category: String,     // category name
    val date: String,         // format: yyyy-MM-dd
    val note: String,
    val timestamp: Long       // System.currentTimeMillis()
)
