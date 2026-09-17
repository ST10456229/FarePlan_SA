package com.example.fareplansa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var spinnerLanguage: Spinner
    private lateinit var spinnerCurrency: Spinner
    private lateinit var btnSave: Button

    private val languages = listOf(
        "English" to "en",
        "isiZulu" to "zu",
        "Afrikaans" to "af"
    )
    private val currencies = listOf("ZAR", "USD", "EUR", "GBP", "AUD")

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        spinnerLanguage = findViewById(R.id.spinnerSettingsLanguage)
        spinnerCurrency = findViewById(R.id.spinnerSettingsCurrency)
        btnSave = findViewById(R.id.btnSaveSettings)

        val prefs = getSharedPreferences("fareplan_prefs", MODE_PRIVATE)
        val currentLang = prefs.getString("language", "en") ?: "en"
        val currentCurrency = prefs.getString("home_currency", "ZAR") ?: "ZAR"

        val langAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            languages.map { it.first }
        )
        spinnerLanguage.adapter = langAdapter
        spinnerLanguage.setSelection(languages.indexOfFirst { it.second == currentLang })

        val currAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            currencies
        )
        spinnerCurrency.adapter = currAdapter
        spinnerCurrency.setSelection(currencies.indexOf(currentCurrency))

        btnSave.setOnClickListener {
            val newLang = languages[spinnerLanguage.selectedItemPosition].second
            val newCurrency = spinnerCurrency.selectedItem.toString()

            prefs.edit()
                .putString("language", newLang)
                .putString("home_currency", newCurrency)
                .apply()

            Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show()

            // Restart the app to apply the new language
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }
}