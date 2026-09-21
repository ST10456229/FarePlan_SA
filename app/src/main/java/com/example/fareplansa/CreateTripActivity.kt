package com.example.fareplansa

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateTripActivity : AppCompatActivity() {

    private lateinit var etDestination: EditText
    private lateinit var etStartDate: EditText
    private lateinit var etEndDate: EditText
    private lateinit var etTotalBudget: EditText
    private lateinit var btnSaveTrip: Button
    private lateinit var btnCancel: Button
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNav: BottomNavigationView

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var startDateMillis: Long = 0L
    private var endDateMillis: Long = 0L

    companion object {
        private const val TAG = "CreateTripActivity"
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_trip)

        etDestination = findViewById(R.id.etDestination)
        etStartDate = findViewById(R.id.etStartDate)
        etEndDate = findViewById(R.id.etEndDate)
        etTotalBudget = findViewById(R.id.etTotalBudget)
        btnSaveTrip = findViewById(R.id.btnSaveTrip)
        btnCancel = findViewById(R.id.btnCancel)
        drawerLayout = findViewById(R.id.drawerLayout)
        bottomNav = findViewById(R.id.bottomNav)

        // Setup Navigation
        NavigationHelper.setupBottomNavigation(this, bottomNav, R.id.nav_plan)
        NavigationHelper.setupDrawer(this, drawerLayout, findViewById(R.id.btnMenu))
        NavigationHelper.setupCommonActions(this)

        // Date pickers
        etStartDate.setOnClickListener { showDatePicker(isStart = true) }
        etEndDate.setOnClickListener { showDatePicker(isStart = false) }

        // Save button
        btnSaveTrip.setOnClickListener { saveTrip() }

        // Cancel button
        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun showDatePicker(isStart: Boolean) {
        val calendar = Calendar.getInstance()

        val dialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val picked = Calendar.getInstance()
                picked.set(year, month, dayOfMonth, 0, 0, 0)
                picked.set(Calendar.MILLISECOND, 0)

                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formatted = formatter.format(picked.time)

                if (isStart) {
                    startDateMillis = picked.timeInMillis
                    etStartDate.setText(formatted)
                } else {
                    endDateMillis = picked.timeInMillis
                    etEndDate.setText(formatted)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.show()
    }

    private fun saveTrip() {
        val destination = etDestination.text.toString().trim()
        val budgetText = etTotalBudget.text.toString().trim()

        // Validation
        if (destination.isEmpty()) {
            Toast.makeText(this, getString(R.string.create_trip_error_destination), Toast.LENGTH_SHORT).show()
            return
        }
        if (startDateMillis == 0L) {
            Toast.makeText(this, getString(R.string.create_trip_error_start), Toast.LENGTH_SHORT).show()
            return
        }
        if (endDateMillis == 0L) {
            Toast.makeText(this, getString(R.string.create_trip_error_end), Toast.LENGTH_SHORT).show()
            return
        }
        if (endDateMillis < startDateMillis) {
            Toast.makeText(this, getString(R.string.create_trip_error_order), Toast.LENGTH_SHORT).show()
            return
        }
        if (budgetText.isEmpty()) {
            Toast.makeText(this, getString(R.string.create_trip_error_budget), Toast.LENGTH_SHORT).show()
            return
        }

        val totalBudget = budgetText.toDoubleOrNull()
        if (totalBudget == null || totalBudget <= 0) {
            Toast.makeText(this, getString(R.string.create_trip_error_budget_valid), Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, getString(R.string.create_trip_error_not_logged_in), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Calculate daily burn rate
        val daysDifference = ((endDateMillis - startDateMillis) / (1000 * 60 * 60 * 24)).toInt() + 1
        val dailyBurnRate = totalBudget / daysDifference

        // Build the Trip object
        val trip = Trip(
            destination = destination,
            startDate = Timestamp(startDateMillis / 1000, 0),
            endDate = Timestamp(endDateMillis / 1000, 0),
            totalBudget = totalBudget,
            remainingBudget = totalBudget,   // Nothing spent yet
            dailyBurnRate = dailyBurnRate,
            currency = "ZAR",
            isComplete = false
        )

        // Write to Firestore under users/{uid}/trips
        db.collection("users")
            .document(userId)
            .collection("trips")
            .add(trip)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Trip saved with ID: ${documentReference.id}")
                Toast.makeText(this, getString(R.string.create_trip_saved), Toast.LENGTH_SHORT).show()
                finish()  // Go back to Dashboard
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error saving trip", e)
                Toast.makeText(this, "Failed to save: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}