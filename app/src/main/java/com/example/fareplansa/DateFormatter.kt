package com.example.fareplansa

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * South African date formatting utility.
 * Always uses DD/MM/YYYY regardless of the user's device locale.
 */
object DateFormatter {

    private val saFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun formatDate(date: Date?): String {
        return date?.let { saFormatter.format(it) } ?: "?"
    }

    fun formatRange(start: Date?, end: Date?): String {
        return "${formatDate(start)} - ${formatDate(end)}"
    }
}