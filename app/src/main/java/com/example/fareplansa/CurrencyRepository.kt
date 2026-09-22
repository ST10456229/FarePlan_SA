package com.example.fareplansa

import android.content.Context
import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CurrencyRepository(private val context: Context) {

    companion object {
        private const val TAG = "CurrencyRepository"
        private const val BASE_URL = "https://v6.exchangerate-api.com/"
        private const val PREFS_NAME = "fareplan_currency"
        private const val KEY_RATES = "cached_rates"
        private const val KEY_BASE = "cached_base"
        private const val KEY_TIMESTAMP = "cached_timestamp"
        private const val CACHE_DURATION_MS = 6 * 60 * 60 * 1000L // 6 hours


        private const val API_KEY = "664f5c346c6405d4e0d2e7b9"
    }

    private val api: CurrencyApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApi::class.java)
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Gets the exchange rate from [from] to [to].
     * Returns null if the rate cannot be determined.
     */
    suspend fun getRate(from: String, to: String): Double? {
        val rates = getRates(from) ?: return null
        return rates[to]
    }

    /**
     * Gets all rates for a base currency, using cache if fresh (< 6 hours old).
     */
    private suspend fun getRates(base: String): Map<String, Double>? {
        // Check cache first
        val cached = getCachedRates(base)
        if (cached != null) {
            Log.d(TAG, "Using cached rates for $base")
            return cached
        }

        // Fetch fresh rates
        return try {
            Log.d(TAG, "Fetching fresh rates for $base")
            val response = api.getLatestRates(API_KEY, base)
            if (response.result == "success") {
                saveRates(base, response.rates)
                response.rates
            } else {
                Log.w(TAG, "API returned result: ${response.result}")
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch rates", e)
            null
        }
    }

    private fun getCachedRates(base: String): Map<String, Double>? {
        val cachedBase = prefs.getString(KEY_BASE, null)
        val timestamp = prefs.getLong(KEY_TIMESTAMP, 0L)

        if (cachedBase != base) return null
        if (System.currentTimeMillis() - timestamp > CACHE_DURATION_MS) return null

        val json = prefs.getString(KEY_RATES, null) ?: return null
        return try {
            val type = object : com.google.gson.reflect.TypeToken<Map<String, Double>>() {}.type
            com.google.gson.Gson().fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }

    private fun saveRates(base: String, rates: Map<String, Double>) {
        val json = com.google.gson.Gson().toJson(rates)
        prefs.edit()
            .putString(KEY_RATES, json)
            .putString(KEY_BASE, base)
            .putLong(KEY_TIMESTAMP, System.currentTimeMillis())
            .apply()
    }
}