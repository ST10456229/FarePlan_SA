package com.example.fareplansa

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        routeUser()
    }

    private fun routeUser() {
        val prefs = getSharedPreferences("fareplan_prefs", MODE_PRIVATE)
        val hasSeenOnboarding = prefs.getBoolean("has_seen_onboarding", false)
        val currentUser = FirebaseAuth.getInstance().currentUser

        val nextActivity = when {
            // 1. First-time user → Onboarding (regardless of login state)
            !hasSeenOnboarding -> OnboardingActivity::class.java

            // 2. Onboarding seen + logged in → Dashboard
            currentUser != null -> DashboardActivity::class.java

            // 3. Onboarding seen + not logged in → Login
            else -> LoginActivity::class.java
        }

        startActivity(Intent(this, nextActivity))
        finish()
    }
}