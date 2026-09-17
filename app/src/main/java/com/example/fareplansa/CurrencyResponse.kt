package com.example.fareplansa

import com.google.gson.annotations.SerializedName

/**
 * Response from ExchangeRate-API's Standard endpoint.
 * Endpoint: https://v6.exchangerate-api.com/v6/YOUR-API-KEY/latest/USD
 */
data class CurrencyResponse(
    @SerializedName("result") val result: String = "",
    @SerializedName("base_code") val baseCode: String = "",
    @SerializedName("time_last_update_unix") val lastUpdateUnix: Long = 0L,
    @SerializedName("conversion_rates") val rates: Map<String, Double> = emptyMap()
)