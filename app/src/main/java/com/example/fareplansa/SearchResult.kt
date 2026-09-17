package com.example.fareplansa

/**
 * Normalized search result — used for both flights and hotels.
 * We map whatever the external API returns into this simple shape.
 */
data class SearchResult(
    val id: String = "",
    val type: String = "",           // "flight" or "hotel"
    val title: String = "",           // e.g. "SAA JNB → CPT"
    val subtitle: String = "",        // e.g. "10 Dec, 08:00"
    val priceInZAR: Double = 0.0,
    val provider: String = "",        // e.g. "Booking.com"
    val deepLink: String = ""         // URL to open for booking
)