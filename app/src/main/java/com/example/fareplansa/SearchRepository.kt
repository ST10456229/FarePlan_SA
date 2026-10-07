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
     */
    suspend fun search(
        type: String,
        query: String,
        maxPrice: Double
    ): List<SearchResult> {
        Log.d(TAG, "===== SEARCH START =====")
        Log.d(TAG, "type='$type' query='$query' maxPrice=$maxPrice")

        val allResults = mockResults(type)
        Log.d(TAG, "mockResults returned ${allResults.size} items")

        val filtered = allResults.filter { it.priceInZAR <= maxPrice }
        Log.d(TAG, "After budget filter: ${filtered.size} items")

        // Fallback: if the filter emptied the list but we have results,
        // return all so the user sees something instead of an empty screen.
        val finalResults = if (filtered.isEmpty() && allResults.isNotEmpty()) {
            Log.d(TAG, "Filter emptied list — returning all as fallback")
            allResults
        } else {
            filtered
        }

        Log.d(TAG, "Returning ${finalResults.size} results")
        Log.d(TAG, "===== SEARCH END =====")
        return finalResults
    }

    /**
     * Mock data — mimics what a real API would return.
     * Handles case-insensitive and plural type values gracefully.
     */
    private fun mockResults(type: String): List<SearchResult> {
        // Normalize the type — trim, lowercase, remove trailing 's'
        val normalized = type.trim().lowercase().removeSuffix("s")
        Log.d(TAG, "Normalized type: '$normalized'")

        return when (normalized) {
            "flight" -> listOf(
                SearchResult(
                    id = "f1", type = "flight",
                    title = "SAA JNB → CPT",
                    subtitle = "10 Dec, 08:00 - 10:15",
                    priceInZAR = 1200.0,
                    provider = "Skyscanner"
                ),
                SearchResult(
                    id = "f2", type = "flight",
                    title = "FlySafair JNB → CPT",
                    subtitle = "10 Dec, 14:00 - 16:10",
                    priceInZAR = 950.0,
                    provider = "Skyscanner"
                ),
                SearchResult(
                    id = "f3", type = "flight",
                    title = "British Airways JNB → CPT",
                    subtitle = "10 Dec, 06:00 - 08:15",
                    priceInZAR = 2800.0,
                    provider = "Skyscanner"
                ),
                SearchResult(
                    id = "f4", type = "flight",
                    title = "Kulula JNB → CPT",
                    subtitle = "10 Dec, 18:00 - 20:10",
                    priceInZAR = 1450.0,
                    provider = "Skyscanner"
                )
            )

            "hotel" -> listOf(
                SearchResult(
                    id = "h1", type = "hotel",
                    title = "The Waterfront Inn",
                    subtitle = "4.5★ · Cape Town",
                    priceInZAR = 1100.0,
                    provider = "Booking.com"
                ),
                SearchResult(
                    id = "h2", type = "hotel",
                    title = "Sea Point Lodge",
                    subtitle = "4.2★ · Cape Town",
                    priceInZAR = 1400.0,
                    provider = "Booking.com"
                ),
                SearchResult(
                    id = "h3", type = "hotel",
                    title = "Budget Backpackers",
                    subtitle = "3.8★ · Cape Town",
                    priceInZAR = 450.0,
                    provider = "Booking.com"
                ),
                SearchResult(
                    id = "h4", type = "hotel",
                    title = "V&A Hotel",
                    subtitle = "4.7★ · Cape Town",
                    priceInZAR = 2200.0,
                    provider = "Booking.com"
                )
            )

            else -> {
                Log.w(TAG, "Unknown type: '$type' (normalized='$normalized') — returning empty")
                emptyList()
            }
        }
    }
}