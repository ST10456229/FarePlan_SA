package com.example.fareplansa

/**
 * Simple utility for converting amounts using a rate map.
 * Rates are relative to a base currency (e.g., USD).
 */
object CurrencyConverter {

    /**
     * Converts [amount] from [fromCurrency] to [toCurrency] using [rates]
     * where rates are keyed relative to a common base.
     */
    fun convert(
        amount: Double,
        fromCurrency: String,
        toCurrency: String,
        rates: Map<String, Double>
    ): Double? {
        if (fromCurrency == toCurrency) return amount

        val fromRate = rates[fromCurrency] ?: return null
        val toRate = rates[toCurrency] ?: return null

        // Convert: amount -> base -> target
        val amountInBase = amount / fromRate
        return amountInBase * toRate
    }
}