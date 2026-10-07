package com.example.fareplansa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var spinnerLanguage: Spinner
    private lateinit var spinnerCurrency: Spinner
    private lateinit var spinnerDateFormat: Spinner
    private lateinit var etDefaultDuration: EditText
    private lateinit var seekAlertThreshold: SeekBar
    private lateinit var tvAlertThresholdValue: TextView
    private lateinit var switchPushNotifications: MaterialSwitch
    private lateinit var switchAutoConvert: MaterialSwitch
    private lateinit var btnClearAllTrips: Button
    private lateinit var btnSave: Button
    private lateinit var tvAppVersion: TextView

    private val prefs by lazy { getSharedPreferences("fareplan_prefs", MODE_PRIVATE) }
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val languages = listOf(
        "English" to "en",
        "isiZulu" to "zu",
        "Afrikaans" to "af"
    )
    private val currencies = listOf("ZAR", "USD", "EUR", "GBP", "AUD")
    private val dateFormats = listOf("DD/MM/YYYY", "MM/DD/YYYY", "YYYY-MM-DD")

    companion object {
        private const val TAG = "SettingsActivity"
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        bindViews()
        setupSpinners()
        loadSavedSettings()
        setupListeners()
    }

    private fun bindViews() {
        btnBack = findViewById(R.id.btnBack)
        spinnerLanguage = findViewById(R.id.spinnerSettingsLanguage)
        spinnerCurrency = findViewById(R.id.spinnerSettingsCurrency)
        spinnerDateFormat = findViewById(R.id.spinnerDateFormat)
        etDefaultDuration = findViewById(R.id.etDefaultDuration)
        seekAlertThreshold = findViewById(R.id.seekAlertThreshold)
        tvAlertThresholdValue = findViewById(R.id.tvAlertThresholdValue)
        switchPushNotifications = findViewById(R.id.switchPushNotifications)
        switchAutoConvert = findViewById(R.id.switchAutoConvert)
        btnClearAllTrips = findViewById(R.id.btnClearAllTrips)
        btnSave = findViewById(R.id.btnSaveSettings)
        tvAppVersion = findViewById(R.id.tvAppVersion)
    }

    private fun setupSpinners() {
        val langAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            languages.map { it.first }
        )
        spinnerLanguage.adapter = langAdapter

        val currAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            currencies
        )
        spinnerCurrency.adapter = currAdapter

        val dateAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            dateFormats
        )
        spinnerDateFormat.adapter = dateAdapter
    }

    private fun loadSavedSettings() {
        // Language
        val langCode = prefs.getString("language", "en") ?: "en"
        val langIndex = languages.indexOfFirst { it.second == langCode }.coerceAtLeast(0)
        spinnerLanguage.setSelection(langIndex)

        // Currency
        val currency = prefs.getString("home_currency", "ZAR") ?: "ZAR"
        val currIndex = currencies.indexOf(currency).coerceAtLeast(0)
        spinnerCurrency.setSelection(currIndex)

        // Date Format
        val dateFormat = prefs.getString("date_format", "DD/MM/YYYY") ?: "DD/MM/YYYY"
        val dateIndex = dateFormats.indexOf(dateFormat).coerceAtLeast(0)
        spinnerDateFormat.setSelection(dateIndex)

        // Default Duration
        val defaultDuration = prefs.getInt("default_trip_duration", 7)
        etDefaultDuration.setText(defaultDuration.toString())

        // Alert Threshold (SeekBar range 50-95)
        val threshold = prefs.getInt("alert_threshold", 70)
        seekAlertThreshold.progress = (threshold - 50).coerceIn(0, 45)
        tvAlertThresholdValue.text = "$threshold%"
        seekAlertThreshold.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = progress + 50
                tvAlertThresholdValue.text = "$value%"
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        // Push Notifications
        switchPushNotifications.isChecked = prefs.getBoolean("push_notifications", true)

        // Auto-Convert
        switchAutoConvert.isChecked = prefs.getBoolean("auto_convert", true)

        // App Version
        tvAppVersion.text = getString(R.string.settings_version_value, "1.0.0")
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }

        btnSave.setOnClickListener { saveAllSettings() }

        btnClearAllTrips.setOnClickListener { confirmClearAllTrips() }
    }

    private fun saveAllSettings() {
        val newLang = languages[spinnerLanguage.selectedItemPosition].second
        val newCurrency = spinnerCurrency.selectedItem.toString()
        val newDateFormat = spinnerDateFormat.selectedItem.toString()

        val durationText = etDefaultDuration.text.toString().trim()
        val newDuration = durationText.toIntOrNull() ?: 7
        if (newDuration < 1 || newDuration > 90) {
            Toast.makeText(this, getString(R.string.settings_invalid_duration), Toast.LENGTH_SHORT).show()
            return
        }

        val newThreshold = seekAlertThreshold.progress + 50
        val pushEnabled = switchPushNotifications.isChecked
        val autoConvert = switchAutoConvert.isChecked

        // Save to SharedPreferences
        prefs.edit()
            .putString("language", newLang)
            .putString("home_currency", newCurrency)
            .putString("date_format", newDateFormat)
            .putInt("default_trip_duration", newDuration)
            .putInt("alert_threshold", newThreshold)
            .putBoolean("push_notifications", pushEnabled)
            .putBoolean("auto_convert", autoConvert)
            .apply()

        // Also save the user's profile settings to Firestore (cloud sync)
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val profileUpdate = mapOf(
                "homeCurrency" to newCurrency,
                "language" to newLang,
                "dateFormat" to newDateFormat,
                "defaultTripDuration" to newDuration,
                "alertThreshold" to newThreshold,
                "pushNotifications" to pushEnabled,
                "autoConvert" to autoConvert
            )
            db.collection("users").document(userId)
                .set(profileUpdate, com.google.firebase.firestore.SetOptions.merge())
        }

        Toast.makeText(this, getString(R.string.settings_saved), Toast.LENGTH_SHORT).show()

        // Restart app to apply language (only if it changed)
        val oldLang = resources.configuration.locales[0].language
        if (newLang != oldLang) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        } else {
            finish()
        }
    }

    private fun confirmClearAllTrips() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.settings_clear_confirm_title))
            .setMessage(getString(R.string.settings_clear_confirm_message))
            .setPositiveButton(getString(R.string.settings_clear_confirm_yes)) { _, _ ->
                clearAllTrips()
            }
            .setNegativeButton(getString(R.string.settings_clear_confirm_cancel), null)
            .show()
    }

    private fun clearAllTrips() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("trips")
            .get()
            .addOnSuccessListener { snapshot ->
                val batch = db.batch()
                snapshot.documents.forEach { doc ->
                    batch.delete(doc.reference)
                }
                batch.commit()
                    .addOnSuccessListener {
                        Toast.makeText(this, getString(R.string.settings_clear_success), Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}