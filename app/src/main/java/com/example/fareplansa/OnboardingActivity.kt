package com.example.fareplansa

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var spinnerCurrency: Spinner
    private lateinit var spinnerLanguage: Spinner
    private lateinit var btnContinue: Button

    private val currencies = listOf("ZAR", "USD", "EUR", "GBP", "AUD")

    private val languages = listOf(
        "English" to "en",
        "isiZulu" to "zu",
        "Afrikaans" to "af"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        spinnerCurrency = findViewById(R.id.spinnerCurrency)
        spinnerLanguage = findViewById(R.id.spinnerLanguage)
        btnContinue = findViewById(R.id.btnContinue)

        // Currency spinner
        val currencyAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            currencies
        )
        spinnerCurrency.adapter = currencyAdapter

        // Language spinner
        val languageNames = languages.map { it.first }
        val languageAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            languageNames
        )
        spinnerLanguage.adapter = languageAdapter

        // Continue button
        btnContinue.setOnClickListener {
            val selectedCurrency = spinnerCurrency.selectedItem.toString()
            val selectedLanguageIndex = spinnerLanguage.selectedItemPosition
            val selectedLanguageCode = languages[selectedLanguageIndex].second

            val prefs = getSharedPreferences("fareplan_prefs", MODE_PRIVATE)
            prefs.edit()
                .putString("home_currency", selectedCurrency)
                .putString("language", selectedLanguageCode)
                .putBoolean("has_seen_onboarding", true)
                .apply()

            Toast.makeText(
                this,
                "Currency: $selectedCurrency, Language: $selectedLanguageCode",
                Toast.LENGTH_SHORT
            ).show()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}