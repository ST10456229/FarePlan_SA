package com.example.fareplansa

import com.google.firebase.Timestamp

data class Expense(
    val expenseId: String = "",
    val category: String = "Custom",       // "Flight", "Hotel/Airbnb", "Car", "Food", "Activity", "Custom"
    val vendor: String = "",                // "Airbnb", "SAA", "Uber", etc.
    val description: String = "",
    val costInZAR: Double = 0.0,
    val isPaid: Boolean = false,
    val date: Timestamp? = null,
    val externalReference: String = ""
)