package com.example.fareplansa

import android.util.Log

class SearchRepository {

    companion object {
        private const val TAG = "SearchRepository"
    }

    /**
     * Search for flights or hotels.
     *
     * ⚠️ For MVP, this returns mock data so the UI can be built and tested.
     * To use real data, replace `mockResults()` with a Retrofit call to RapidAPI.
     */
    suspend fun search(
        type: String,           // "flight" or "hotel"
        query: String,
        maxPrice: Double
    ): List<SearchResult> {
        Log.d(TAG, "Search: type=$type query=$query maxPrice=$maxPrice")

        // TODO: Replace with real RapidAPI call
        // Example:
        // val api = RapidApiClient.instance.create(SearchApi::class.java)
        // val response = api.searchFlights(RapidApiClient.API_KEY, RapidApiClient.API_HOST, query, ...)
        // return response.map { ... }

        return mockResults(type, query, maxPrice)
    }

    /**
     * Temporary mock data — mimics what a real API would return.
     * All results respect the budget cap.
     */
    private fun mockResults(type: String, query: String, maxPrice: Double): List<SearchResult> {
        val all = when (type) {
            "flight" -> listOf(
                SearchResult(
                    id = "f1", type = "flight",
                    title = "SAA JNB → CPT",
                    subtitle = "10 Dec, 08:00 - 10:15",
                    priceInZAR = 1200.0,
                    provider = "Skyscanner",
                    deepLink = "https://www.skyscanner.co.za/"
                ),
                SearchResult(
                    id = "f2", type = "flight",
                    title = "FlySafair JNB → CPT",
                    subtitle = "10 Dec, 14:00 - 16:10",
                    priceInZAR = 950.0,
                    provider = "Skyscanner",
                    deepLink = "https://www.skyscanner.co.za/"
                ),
                SearchResult(
                    id = "f3", type = "flight",
                    title = "British Airways JNB → CPT",
                    subtitle = "10 Dec, 06:00 - 08:15",
                    priceInZAR = 2800.0,
                    provider = "Skyscanner",
                    deepLink = "https://www.skyscanner.co.za/"
                )
            )
            "hotel" -> listOf(
                SearchResult(
                    id = "h1", type = "hotel",
                    title = "The Waterfront Inn",
                    subtitle = "4.5★ · Cape Town",
                    priceInZAR = 1100.0,
                    provider = "Booking.com",
                    deepLink = "https://www.booking.com/"
                ),
                SearchResult(
                    id = "h2", type = "hotel",
                    title = "Sea Point Lodge",
                    subtitle = "4.2★ · Cape Town",
                    priceInZAR = 1400.0,
                    provider = "Booking.com",
                    deepLink = "https://www.booking.com/"
                ),
                SearchResult(
                    id = "h3", type = "hotel",
                    title = "Budget Backpackers",
                    subtitle = "3.8★ · Cape Town",
                    priceInZAR = 450.0,
                    provider = "Booking.com",
                    deepLink = "https://www.booking.com/"
                )
            )
            else -> emptyList()
        }

        // 🔑 DYNAMIC BUDGET TETHERING: only return results within budget
        return all.filter { it.priceInZAR <= maxPrice }
    }
}