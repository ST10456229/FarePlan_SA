package com.example.fareplansa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import android.content.Context

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var btnNewTrip: Button
    private lateinit var btnLogout: Button
    private lateinit var recyclerTrips: RecyclerView
    private lateinit var tvEmptyState: TextView

    // Add binding
    private lateinit var btnSettings: Button


    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val TAG = "DashboardActivity"
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        tvWelcome = findViewById(R.id.tvWelcome)
        btnNewTrip = findViewById(R.id.btnNewTrip)
        btnLogout = findViewById(R.id.btnLogout)
        recyclerTrips = findViewById(R.id.recyclerTrips)
        tvEmptyState = findViewById(R.id.tvEmptyState)

        recyclerTrips.layoutManager = LinearLayoutManager(this)

        val user = auth.currentUser
        val displayName = user?.email ?: getString(R.string.dashboard_traveller)
        tvWelcome.text = getString(R.string.dashboard_hello, displayName)

        // Save FCM token (so Cloud Function can send budget alerts)
        fetchAndSaveFcmToken()

        btnNewTrip.setOnClickListener {
            startActivity(Intent(this, CreateTripActivity::class.java))
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, getString(R.string.dashboard_logged_out), Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnSettings = findViewById(R.id.btnSettings)

// Add click listener
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadTrips()
    }

    /**
     * Fetches the device's FCM token and stores it in Firestore under users/{uid}.fcmToken.
     * The Cloud Function (budgetAlert) reads this token to send push notifications
     * when the trip's spend crosses 80%, 90%, or 100%.
     */
    private fun fetchAndSaveFcmToken() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.w(TAG, "Cannot fetch FCM token — user not logged in")
            return
        }

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                Log.d(TAG, "FCM token: $token")
                db.collection("users")
                    .document(userId)
                    .set(mapOf("fcmToken" to token), SetOptions.merge())
                    .addOnSuccessListener { Log.d(TAG, "FCM token saved") }
                    .addOnFailureListener { e -> Log.w(TAG, "Failed to save FCM token", e) }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Failed to fetch FCM token", e)
            }
    }

    private fun loadTrips() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users")
            .document(userId)
            .collection("trips")
            .orderBy("startDate", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val trips = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Trip::class.java)?.copy(tripId = doc.id)
                }

                if (trips.isEmpty()) {
                    tvEmptyState.visibility = View.VISIBLE
                    recyclerTrips.visibility = View.GONE
                } else {
                    tvEmptyState.visibility = View.GONE
                    recyclerTrips.visibility = View.VISIBLE
                }

                recyclerTrips.adapter = TripAdapter(trips) { trip ->
                    val intent = Intent(this, TripDetailActivity::class.java)
                    intent.putExtra(TripDetailActivity.EXTRA_TRIP_ID, trip.tripId)
                    intent.putExtra(TripDetailActivity.EXTRA_DESTINATION, trip.destination)
                    intent.putExtra(
                        TripDetailActivity.EXTRA_START_DATE,
                        trip.startDate?.seconds?.times(1000) ?: 0L
                    )
                    intent.putExtra(
                        TripDetailActivity.EXTRA_END_DATE,
                        trip.endDate?.seconds?.times(1000) ?: 0L
                    )
                    intent.putExtra(TripDetailActivity.EXTRA_TOTAL_BUDGET, trip.totalBudget)
                    intent.putExtra(TripDetailActivity.EXTRA_REMAINING_BUDGET, trip.remainingBudget)
                    intent.putExtra(TripDetailActivity.EXTRA_DAILY_BURN, trip.dailyBurnRate)
                    startActivity(intent)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Failed to load trips", e)
                Toast.makeText(this, "Failed to load trips: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}