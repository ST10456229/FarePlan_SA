package com.example.fareplansa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
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
    private lateinit var emptyStateContainer: LinearLayout
    private lateinit var progressDashboard: ProgressBar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var tvViewAll: TextView
    private lateinit var tvTotalBudget: TextView
    private lateinit var tvTotalSpent: TextView

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

        // Bind views
        tvWelcome = findViewById(R.id.tvWelcome)
        btnNewTrip = findViewById(R.id.btnNewTrip)
        recyclerTrips = findViewById(R.id.recyclerTrips)
        emptyStateContainer = findViewById(R.id.emptyStateContainer)
        progressDashboard = findViewById(R.id.progressDashboard)
        drawerLayout = findViewById(R.id.drawerLayout)
        bottomNav = findViewById(R.id.bottomNav)
        fabAdd = findViewById(R.id.fabAdd)
        tvViewAll = findViewById(R.id.tvViewAll)
        tvTotalBudget = findViewById(R.id.tvTotalBudget)
        tvTotalSpent = findViewById(R.id.tvTotalSpent)

        recyclerTrips.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Setup navigation
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
        loadRecentExpenses()
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

                // Update summary totals
                var totalBudget = 0.0
                var totalSpent = 0.0
                trips.forEach { trip ->
                    totalBudget += trip.totalBudget
                    totalSpent += (trip.totalBudget - trip.remainingBudget)
                }

                tvTotalBudget.text = "R%,.2f".format(totalBudget)
                tvTotalSpent.text = "R%,.2f".format(totalSpent)

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

    /**
     * Loads the 3 most recent expenses across all of the user's trips
     * and displays them in the Recent Activity card.
     */
    private fun loadRecentExpenses() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("trips")
            .get()
            .addOnSuccessListener { tripsSnapshot ->
                val allExpenses = mutableListOf<Triple<String, Expense, String>>()

                if (tripsSnapshot.isEmpty) {
                    updateRecentExpensesUI(emptyList())
                    return@addOnSuccessListener
                }

                var completedQueries = 0
                val totalTrips = tripsSnapshot.size()

                tripsSnapshot.documents.forEach { tripDoc ->
                    val destination = tripDoc.getString("destination") ?: "Trip"

                    tripDoc.reference.collection("expenses")
                        .orderBy("date", Query.Direction.DESCENDING)
                        .limit(3)
                        .get()
                        .addOnSuccessListener { expSnapshot ->
                            expSnapshot.documents.forEach { expDoc ->
                                val expense = expDoc.toObject(Expense::class.java)
                                if (expense != null) {
                                    allExpenses.add(Triple(destination, expense, tripDoc.id))
                                }
                            }
                            completedQueries++
                            if (completedQueries == totalTrips) {
                                val top3 = allExpenses
                                    .sortedByDescending { it.second.date?.seconds ?: 0 }
                                    .take(3)
                                updateRecentExpensesUI(top3)
                            }
                        }
                        .addOnFailureListener {
                            completedQueries++
                            if (completedQueries == totalTrips) {
                                val top3 = allExpenses
                                    .sortedByDescending { it.second.date?.seconds ?: 0 }
                                    .take(3)
                                updateRecentExpensesUI(top3)
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Failed to load recent expenses", e)
            }
    }

    private fun updateRecentExpensesUI(items: List<Triple<String, Expense, String>>) {
        val container = findViewById<LinearLayout>(R.id.recentExpensesList)
        val emptyView = findViewById<TextView>(R.id.tvRecentEmpty)

        container.removeAllViews()

        if (items.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            container.visibility = View.GONE
            return
        }

        emptyView.visibility = View.GONE
        container.visibility = View.VISIBLE

        items.forEachIndexed { index, (destination, expense, _) ->
            val row = layoutInflater.inflate(R.layout.item_recent_expense, container, false)
            row.findViewById<TextView>(R.id.tvRecentVendor).text = expense.vendor
            row.findViewById<TextView>(R.id.tvRecentSubtitle).text =
                "$destination · ${expense.category}"
            row.findViewById<TextView>(R.id.tvRecentAmount).text =
                "-R%,.2f".format(expense.costInZAR)

            container.addView(row)

            if (index < items.size - 1) {
                val divider = View(this)
                divider.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1
                )
                divider.setBackgroundColor(0xFFE3E8EE.toInt())
                container.addView(divider)
            }
        }
    }
}