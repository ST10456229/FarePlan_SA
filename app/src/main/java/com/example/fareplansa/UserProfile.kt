package com.example.fareplansa

import com.google.firebase.Timestamp

data class UserProfile(
    val userId: String = "",
    val displayName: String = "",
    val homeCurrency: String = "ZAR",
    val language: String = "en",
    val createdAt: Timestamp? = null
)