package com.example.fareplansa

import org.junit.Assert.*
import org.junit.Test

class CurrencyConverterTest {

    private val rates = mapOf(
        "USD" to 1.0,
        "ZAR" to 18.5,
        "EUR" to 0.92
    )

    @Test
    fun convert_sameCurrency_returnsSameAmount() {
        val result = CurrencyConverter.convert(100.0, "USD", "USD", rates)
        assertEquals(100.0, result!!, 0.001)
    }

    @Test
    fun convert_usdToZar_multipliesByRate() {
        val result = CurrencyConverter.convert(100.0, "USD", "ZAR", rates)
        assertEquals(1850.0, result!!, 0.001)
    }

    @Test
    fun convert_zarToUsd_dividesByRate() {
        val result = CurrencyConverter.convert(1850.0, "ZAR", "USD", rates)
        assertEquals(100.0, result!!, 0.001)
    }

    @Test
    fun convert_unknownCurrency_returnsNull() {
        val result = CurrencyConverter.convert(100.0, "USD", "XYZ", rates)
        assertNull(result)
    }
}