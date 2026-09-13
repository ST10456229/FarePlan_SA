package com.example.fareplansa

import com.google.firebase.Timestamp

data class Trip(
    val tripId: String = "",
    val destination: String = "",
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val totalBudget: Double = 0.0,
    val remainingBudget: Double = 0.0,
    val dailyBurnRate: Double = 0.0,
    val currency: String = "ZAR",
    val isComplete: Boolean = false
)