package com.example.fareplansa

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var startDateMillis: Long = 0L
    private var endDateMillis: Long = 0L

    companion object {
        private const val TAG = "CreateTripActivity"
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
            Toast.makeText(this, "Please enter a destination", Toast.LENGTH_SHORT).show()
            return
        }
        if (startDateMillis == 0L) {
            Toast.makeText(this, "Please pick a start date", Toast.LENGTH_SHORT).show()
            return
        }
        if (endDateMillis == 0L) {
            Toast.makeText(this, "Please pick an end date", Toast.LENGTH_SHORT).show()
            return
        }
        if (endDateMillis < startDateMillis) {
            Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show()
            return
        }
        if (budgetText.isEmpty()) {
            Toast.makeText(this, "Please enter a total budget", Toast.LENGTH_SHORT).show()
            return
        }

        val totalBudget = budgetText.toDoubleOrNull()
        if (totalBudget == null || totalBudget <= 0) {
            Toast.makeText(this, "Please enter a valid budget", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "You must be logged in", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Trip saved!", Toast.LENGTH_SHORT).show()
                finish()  // Go back to Dashboard
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error saving trip", e)
                Toast.makeText(this, "Failed to save: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}