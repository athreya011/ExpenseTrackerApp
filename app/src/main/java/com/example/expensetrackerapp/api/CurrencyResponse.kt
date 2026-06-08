package com.example.expensetrackerapp.api

data class CurrencyResponse(
    val result: String,
    val conversion_rates: Map<String, Double>
)