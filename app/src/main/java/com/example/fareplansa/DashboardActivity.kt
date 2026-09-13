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

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var btnNewTrip: Button
    private lateinit var btnLogout: Button
    private lateinit var recyclerTrips: RecyclerView
    private lateinit var tvEmptyState: TextView

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val TAG = "DashboardActivity"
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
        tvWelcome.text = "Hello, ${user?.email ?: "Traveller"}!"

        btnNewTrip.setOnClickListener {
            startActivity(Intent(this, CreateTripActivity::class.java))
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload trips every time the Dashboard comes back into view
        // (e.g., after the user creates a new trip)
        loadTrips()
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
                    // Placeholder for Phase 4 - open trip detail
                    Toast.makeText(
                        this,
                        "Tapped: ${trip.destination}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Failed to load trips", e)
                Toast.makeText(this, "Failed to load trips: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}