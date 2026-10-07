package com.example.fareplansa

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var spinnerCategory: Spinner
    private lateinit var etVendor: EditText
    private lateinit var etDescription: EditText
    private lateinit var etCost: EditText
    private lateinit var etExpenseDate: EditText
    private lateinit var cbIsPaid: CheckBox
    private lateinit var btnSaveExpense: Button
    private lateinit var btnCancelExpense: Button
    private lateinit var tvConvertedAmount: TextView

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var currencyRepo: CurrencyRepository

    private var tripId: String = ""
    private var dateMillis: Long = 0L
    private var source: String = "manual"       // "manual" | "search"
    private var alsoAddToItinerary: Boolean = false  // true when launched from search

    private val categories = listOf(
        "Flight", "Hotel/Airbnb", "Car", "Food", "Activity", "Custom"
    )

    companion object {
        private const val TAG = "AddExpenseActivity"
        const val EXTRA_TRIP_ID = "extra_trip_id"

        // Pre-fill extras (used when launched from Search)
        const val EXTRA_PREFILL_CATEGORY = "prefill_category"
        const val EXTRA_PREFILL_VENDOR = "prefill_vendor"
        const val EXTRA_PREFILL_DESCRIPTION = "prefill_description"
        const val EXTRA_PREFILL_COST = "prefill_cost"
        const val EXTRA_PREFILL_DATE_MILLIS = "prefill_date_millis"
        const val EXTRA_ADD_TO_ITINERARY = "extra_add_to_itinerary"
    }

    override fun attachBaseContext(newBase: android.content.Context) {
        super.attachBaseContext(LocaleHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        spinnerCategory = findViewById(R.id.spinnerCategory)
        etVendor = findViewById(R.id.etVendor)
        etDescription = findViewById(R.id.etDescription)
        etCost = findViewById(R.id.etCost)
        etExpenseDate = findViewById(R.id.etExpenseDate)
        cbIsPaid = findViewById(R.id.cbIsPaid)
        btnSaveExpense = findViewById(R.id.btnSaveExpense)
        btnCancelExpense = findViewById(R.id.btnCancelExpense)
        tvConvertedAmount = findViewById(R.id.tvConvertedAmount)

        tripId = intent.getStringExtra(EXTRA_TRIP_ID) ?: ""
        currencyRepo = CurrencyRepository(this)

        // Set up category spinner
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )
        spinnerCategory.adapter = adapter

        // Pre-fill from intent (when launched from Search)
        applyPrefill()

        etExpenseDate.setOnClickListener { showDatePicker() }
        btnSaveExpense.setOnClickListener { saveExpense() }
        btnCancelExpense.setOnClickListener { finish() }

        etCost.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) showConversionPreview()
        }
    }

    private fun applyPrefill() {
        val prefillCategory = intent.getStringExtra(EXTRA_PREFILL_CATEGORY)
        val prefillVendor = intent.getStringExtra(EXTRA_PREFILL_VENDOR)
        val prefillDescription = intent.getStringExtra(EXTRA_PREFILL_DESCRIPTION)
        val prefillCost = intent.getDoubleExtra(EXTRA_PREFILL_COST, -1.0)
        val prefillDateMillis = intent.getLongExtra(EXTRA_PREFILL_DATE_MILLIS, 0L)
        alsoAddToItinerary = intent.getBooleanExtra(EXTRA_ADD_TO_ITINERARY, false)

        prefillCategory?.let {
            val idx = categories.indexOf(it)
            if (idx >= 0) spinnerCategory.setSelection(idx)
            source = "search"
        }
        prefillVendor?.let { etVendor.setText(it) }
        prefillDescription?.let { etDescription.setText(it) }
        if (prefillCost >= 0) etCost.setText("%.2f".format(prefillCost))
        if (prefillDateMillis > 0) {
            dateMillis = prefillDateMillis
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            etExpenseDate.setText(formatter.format(prefillDateMillis))
        }

        // For search-sourced items, tick "already paid" by default
        if (source == "search") cbIsPaid.isChecked = false
    }

    private fun showConversionPreview() {
        val costText = etCost.text.toString().trim()
        if (costText.isEmpty()) return
        val cost = costText.toDoubleOrNull() ?: return

        val prefs = getSharedPreferences("fareplan_prefs", MODE_PRIVATE)
        val homeCurrency = prefs.getString("home_currency", "ZAR") ?: "ZAR"
        if (homeCurrency == "ZAR") {
            tvConvertedAmount.visibility = android.view.View.GONE
            return
        }

        lifecycleScope.launch {
            val rate = currencyRepo.getRate(homeCurrency, "ZAR")
            if (rate != null) {
                val converted = cost * rate
                tvConvertedAmount.text = "≈ R%,.2f (at %.4f)".format(converted, rate)
                tvConvertedAmount.visibility = android.view.View.VISIBLE
            } else {
                tvConvertedAmount.visibility = android.view.View.GONE
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val picked = Calendar.getInstance()
                picked.set(year, month, dayOfMonth, 0, 0, 0)
                picked.set(Calendar.MILLISECOND, 0)
                dateMillis = picked.timeInMillis
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                etExpenseDate.setText(formatter.format(picked.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }

    private fun saveExpense() {
        val category = spinnerCategory.selectedItem.toString()
        val vendor = etVendor.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val costText = etCost.text.toString().trim()
        val isPaid = cbIsPaid.isChecked

        // Validation
        if (vendor.isEmpty()) {
            Toast.makeText(this, getString(R.string.add_expense_vendor), Toast.LENGTH_SHORT).show()
            return
        }
        if (costText.isEmpty()) {
            Toast.makeText(this, getString(R.string.add_expense_cost), Toast.LENGTH_SHORT).show()
            return
        }
        val cost = costText.toDoubleOrNull()
        if (cost == null || cost <= 0) {
            Toast.makeText(this, getString(R.string.add_expense_cost), Toast.LENGTH_SHORT).show()
            return
        }
        if (dateMillis == 0L) {
            Toast.makeText(this, getString(R.string.add_expense_date), Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, getString(R.string.create_trip_error_not_logged_in), Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        if (tripId.isEmpty()) {
            Toast.makeText(this, "Invalid trip", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val expense = Expense(
            category = category,
            vendor = vendor,
            description = description,
            costInZAR = cost,
            isPaid = isPaid,
            date = Timestamp(dateMillis / 1000, 0)
        )

        val tripRef = db.collection("users").document(userId)
            .collection("trips").document(tripId)

        // Run a batch: add expense + optionally add itinerary + update trip budget
        db.runBatch { batch ->
            val expenseRef = tripRef.collection("expenses").document()
            batch.set(expenseRef, expense)

            // Update the trip's remaining budget
            batch.update(
                tripRef,
                "remainingBudget",
                com.google.firebase.firestore.FieldValue.increment(-cost)
            )

            // If this booking came from search, also add it to the itinerary
            if (alsoAddToItinerary) {
                val itineraryRef = tripRef.collection("itinerary").document()
                val itineraryItem = mapOf(
                    "title" to "$vendor — $description",
                    "notes" to "Booked via ${if (source == "search") "Search" else "manual"}",
                    "date" to Timestamp(dateMillis / 1000, 0),
                    "time" to "",
                    "isDone" to false,
                    "linkedExpenseId" to expenseRef.id
                )
                batch.set(itineraryRef, itineraryItem)
            }
        }.addOnSuccessListener {
            Log.d(TAG, "Expense saved and budget updated (source=$source)")
            Toast.makeText(this, getString(R.string.add_expense_saved), Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener { e ->
            Log.w(TAG, "Failed to save expense", e)
            Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}