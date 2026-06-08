package com.example.expensetrackerapp.utils

/**
 * Currency exchange rates relative to INR (Indian Rupee).
 * Rates are approximate and static for offline use.
 */
object CurrencyUtils {

    // 1 INR = X of target currency
    private val ratesFromINR = mapOf(
        "₹" to 1.0,
        "$" to 0.012,   // 1 INR ≈ 0.012 USD
        "€" to 0.011,   // 1 INR ≈ 0.011 EUR
        "£" to 0.0095   // 1 INR ≈ 0.0095 GBP
    )

    /** Converts an INR amount to the target currency. */
    fun convert(amountInINR: Double, toCurrency: String): Double {
        val rate = ratesFromINR[toCurrency] ?: 1.0
        return amountInINR * rate
    }

    /** Formats a converted amount with the currency symbol. */
    fun format(amountInINR: Double, currencySymbol: String): String {
        val converted = convert(amountInINR, currencySymbol)
        return "$currencySymbol %.2f".format(converted)
    }
}
