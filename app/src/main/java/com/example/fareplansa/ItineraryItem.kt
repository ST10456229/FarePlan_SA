package com.example.fareplansa

import com.google.firebase.Timestamp

data class ItineraryItem(
    val itemId: String = "",
    val title: String = "",                 // e.g., "Visit Table Mountain"
    val notes: String = "",                 // e.g., "Bring water and sunscreen"
    val date: Timestamp? = null,            // which day
    val time: String = "",                  // "09:00" (optional)
    val isDone: Boolean = false,            // checkbox state
    val linkedExpenseId: String? = null     // optional link to an expense
)