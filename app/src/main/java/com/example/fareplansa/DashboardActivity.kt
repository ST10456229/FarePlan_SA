package com.example.fareplansa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
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
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var btnNewTrip: Button
    private lateinit var recyclerTrips: RecyclerView
    private lateinit var emptyStateContainer: android.widget.LinearLayout
    private lateinit var progressDashboard: ProgressBar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var tvViewAll: TextView

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
        recyclerTrips = findViewById(R.id.recyclerTrips)
        emptyStateContainer = findViewById(R.id.emptyStateContainer)
        progressDashboard = findViewById(R.id.progressDashboard)
        drawerLayout = findViewById(R.id.drawerLayout)
        bottomNav = findViewById(R.id.bottomNav)
        fabAdd = findViewById(R.id.fabAdd)
        tvViewAll = findViewById(R.id.tvViewAll)

        recyclerTrips.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Setup Navigation
        NavigationHelper.setupBottomNavigation(this, bottomNav, R.id.nav_home)
        NavigationHelper.setupDrawer(this, drawerLayout, findViewById(R.id.btnMenu))
        NavigationHelper.setupCommonActions(this)

        val user = auth.currentUser
        val displayName = user?.email ?: getString(R.string.dashboard_traveller)
        tvWelcome.text = getString(R.string.dashboard_hello, displayName)

        fetchAndSaveFcmToken()

        btnNewTrip.setOnClickListener {
            startActivity(Intent(this, CreateTripActivity::class.java))
        }

        fabAdd.setOnClickListener {
            startActivity(Intent(this, CreateTripActivity::class.java))
        }

        tvViewAll.setOnClickListener {
            val intent = Intent(this, TripsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadTrips()
    }

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

        progressDashboard.visibility = View.VISIBLE
        emptyStateContainer.visibility = View.GONE
        recyclerTrips.visibility = View.GONE

        db.collection("users")
            .document(userId)
            .collection("trips")
            .orderBy("startDate", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                progressDashboard.visibility = View.GONE

                val trips = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Trip::class.java)?.copy(tripId = doc.id)
                }

                if (trips.isEmpty()) {
                    emptyStateContainer.visibility = View.VISIBLE
                    recyclerTrips.visibility = View.GONE
                } else {
                    emptyStateContainer.visibility = View.GONE
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
                progressDashboard.visibility = View.GONE
                Log.w(TAG, "Failed to load trips", e)
                Toast.makeText(this, "Failed to load trips: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}