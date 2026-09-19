package com.example.fareplansa

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddItineraryItemActivity : AppCompatActivity() {

    private lateinit var tvFormTitle: TextView
    private lateinit var etTitle: EditText
    private lateinit var etNotes: EditText
    private lateinit var etDate: EditText
    private lateinit var etTime: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var tripId: String = ""
    private var itemId: String? = null         // null for new item, non-null for edit
    private var dateMillis: Long = 0L
    private var timeString: String = ""

    companion object {
        const val EXTRA_TRIP_ID = "extra_trip_id"
        const val EXTRA_ITEM_ID = "extra_item_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_NOTES = "extra_notes"
        const val EXTRA_DATE_MILLIS = "extra_date_millis"
        const val EXTRA_TIME = "extra_time"
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_itinerary_item)

        tvFormTitle = findViewById(R.id.tvItineraryFormTitle)
        etTitle = findViewById(R.id.etItineraryTitle)
        etNotes = findViewById(R.id.etItineraryNotes)
        etDate = findViewById(R.id.etItineraryDate)
        etTime = findViewById(R.id.etItineraryTime)
        btnSave = findViewById(R.id.btnSaveItineraryItem)
        btnCancel = findViewById(R.id.btnCancelItineraryItem)

        tripId = intent.getStringExtra(EXTRA_TRIP_ID) ?: ""
        itemId = intent.getStringExtra(EXTRA_ITEM_ID)

        // If editing, pre-fill fields
        if (itemId != null) {
            tvFormTitle.text = getString(R.string.itinerary_edit_title)
            etTitle.setText(intent.getStringExtra(EXTRA_TITLE) ?: "")
            etNotes.setText(intent.getStringExtra(EXTRA_NOTES) ?: "")

            val millis = intent.getLongExtra(EXTRA_DATE_MILLIS, 0L)
            if (millis > 0) {
                dateMillis = millis
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                etDate.setText(formatter.format(millis))
            }

            val time = intent.getStringExtra(EXTRA_TIME) ?: ""
            if (time.isNotBlank()) {
                timeString = time
                etTime.setText(time)
            }
        }

        etDate.setOnClickListener { showDatePicker() }
        etTime.setOnClickListener { showTimePicker() }
        btnSave.setOnClickListener { saveItem() }
        btnCancel.setOnClickListener { finish() }
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
                etDate.setText(formatter.format(picked.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val dialog = TimePickerDialog(
            this,
            { _, hour, minute ->
                timeString = "%02d:%02d".format(hour, minute)
                etTime.setText(timeString)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        dialog.show()
    }

    private fun saveItem() {
        val title = etTitle.text.toString().trim()
        val notes = etNotes.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(this, getString(R.string.itinerary_error_title), Toast.LENGTH_SHORT).show()
            return
        }
        if (dateMillis == 0L) {
            Toast.makeText(this, getString(R.string.itinerary_error_date), Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid ?: return

        val tripRef = db.collection("users").document(userId)
            .collection("trips").document(tripId)

        val item = ItineraryItem(
            title = title,
            notes = notes,
            date = Timestamp(dateMillis / 1000, 0),
            time = timeString,
            isDone = false
        )

        val task = if (itemId == null) {
            // Create new
            tripRef.collection("itinerary").add(item)
        } else {
            // Update existing
            tripRef.collection("itinerary").document(itemId!!).set(item)
        }

        task.addOnSuccessListener {
            Toast.makeText(this, getString(R.string.itinerary_saved), Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener { e ->
            Toast.makeText(this, getString(R.string.itinerary_error_save, e.message ?: ""), Toast.LENGTH_LONG).show()
        }
    }
}