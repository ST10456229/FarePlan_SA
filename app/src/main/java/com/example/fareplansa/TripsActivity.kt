package com.example.fareplansa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TripsActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerTrips: RecyclerView
    private lateinit var progressTrips: ProgressBar
    private lateinit var tvEmptyTrips: TextView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fabAdd: FloatingActionButton

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips)

        drawerLayout = findViewById(R.id.drawerLayout)
        recyclerTrips = findViewById(R.id.recyclerTrips)
        progressTrips = findViewById(R.id.progressTrips)
        tvEmptyTrips = findViewById(R.id.tvEmptyTrips)
        bottomNav = findViewById(R.id.bottomNav)
        fabAdd = findViewById(R.id.fabAdd)

        recyclerTrips.layoutManager = LinearLayoutManager(this)

        NavigationHelper.setupBottomNavigation(this, bottomNav, R.id.nav_trips)
        NavigationHelper.setupDrawer(this, drawerLayout, findViewById(R.id.btnMenu))
        NavigationHelper.setupCommonActions(this)

        fabAdd.setOnClickListener {
            startActivity(Intent(this, CreateTripActivity::class.java))
        }

        loadTrips()
    }

    private fun loadTrips() {
        val userId = auth.currentUser?.uid ?: return

        progressTrips.visibility = View.VISIBLE
        db.collection("users")
            .document(userId)
            .collection("trips")
            .orderBy("startDate", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                progressTrips.visibility = View.GONE
                val trips = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Trip::class.java)?.copy(tripId = doc.id)
                }

                if (trips.isEmpty()) {
                    tvEmptyTrips.visibility = View.VISIBLE
                    recyclerTrips.visibility = View.GONE
                } else {
                    tvEmptyTrips.visibility = View.GONE
                    recyclerTrips.visibility = View.VISIBLE
                    recyclerTrips.adapter = TripAdapter(trips) { trip ->
                        val intent = Intent(this, TripDetailActivity::class.java)
                        intent.putExtra(TripDetailActivity.EXTRA_TRIP_ID, trip.tripId)
                        // ... pass other extras if needed or fetch in Detail
                        startActivity(intent)
                    }
                }
            }
            .addOnFailureListener {
                progressTrips.visibility = View.GONE
                Toast.makeText(this, "Error loading trips", Toast.LENGTH_SHORT).show()
            }
    }
}