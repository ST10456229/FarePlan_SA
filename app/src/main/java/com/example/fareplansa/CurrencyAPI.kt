package com.example.fareplansa

import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApi {

    /**
     * Fetches all exchange rates with the given base currency.
     * The API key is embedded in the URL path.
     */
    @GET("v6/{apiKey}/latest/{baseCurrency}")
    suspend fun getLatestRates(
        @Path("apiKey") apiKey: String,
        @Path("baseCurrency") baseCurrency: String
    ): CurrencyResponse
}