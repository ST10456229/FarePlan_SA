package com.example.fareplansa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        routeUser()
    }

    private fun routeUser() {
        val prefs = getSharedPreferences("fareplan_prefs", MODE_PRIVATE)
        val hasSeenOnboarding = prefs.getBoolean("has_seen_onboarding", false)
        val currentUser = FirebaseAuth.getInstance().currentUser

        val nextActivity = when {
            !hasSeenOnboarding -> OnboardingActivity::class.java
            currentUser != null -> DashboardActivity::class.java
            else -> LoginActivity::class.java
        }

        startActivity(Intent(this, nextActivity))
        finish()
    }
}