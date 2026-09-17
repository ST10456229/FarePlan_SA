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
import android.content.Context

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

    private val categories = listOf(
        "Flight", "Hotel/Airbnb", "Car", "Food", "Activity", "Custom"
    )

    companion object {
        private const val TAG = "AddExpenseActivity"
        const val EXTRA_TRIP_ID = "extra_trip_id"
    }

    override fun attachBaseContext(newBase: Context) {
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

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )
        spinnerCategory.adapter = adapter

        etExpenseDate.setOnClickListener { showDatePicker() }
        btnSaveExpense.setOnClickListener { saveExpense() }
        btnCancelExpense.setOnClickListener { finish() }

        // Show conversion preview when cost changes
        etCost.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) showConversionPreview()
        }
    }

    private fun showConversionPreview() {
        val costText = etCost.text.toString().trim()
        if (costText.isEmpty()) return

        val cost = costText.toDoubleOrNull() ?: return

        // Get user's home currency from prefs (default ZAR)
        val prefs = getSharedPreferences("fareplan_prefs", MODE_PRIVATE)
        val homeCurrency = prefs.getString("home_currency", "ZAR") ?: "ZAR"

        // If already ZAR, no conversion needed
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

        if (vendor.isEmpty()) {
            Toast.makeText(this, "Please enter a vendor", Toast.LENGTH_SHORT).show()
            return
        }
        if (costText.isEmpty()) {
            Toast.makeText(this, "Please enter a cost", Toast.LENGTH_SHORT).show()
            return
        }
        val cost = costText.toDoubleOrNull()
        if (cost == null || cost <= 0) {
            Toast.makeText(this, "Please enter a valid cost", Toast.LENGTH_SHORT).show()
            return
        }
        if (dateMillis == 0L) {
            Toast.makeText(this, "Please pick a date", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        if (tripId.isEmpty()) {
            Toast.makeText(this, "Invalid trip", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // For MVP, assume cost is entered in ZAR. In a later phase,
        // we can add a currency selector and convert before saving.
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

        db.runBatch { batch ->
            val expenseRef = tripRef.collection("expenses").document()
            batch.set(expenseRef, expense)
            batch.update(tripRef, "remainingBudget",
                com.google.firebase.firestore.FieldValue.increment(-cost))
        }.addOnSuccessListener {
            Log.d(TAG, "Expense saved and budget updated")
            Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener { e ->
            Log.w(TAG, "Failed to save expense", e)
            Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}