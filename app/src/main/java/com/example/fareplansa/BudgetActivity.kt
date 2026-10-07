package com.example.fareplansa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.fareplansa.BudgetCategoryAdapter
import com.example.fareplansa.CategoryTotal

class BudgetActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var progressBudget: ProgressBar
    private lateinit var emptyBudgetState: LinearLayout
    private lateinit var totalSummaryCard: com.google.android.material.card.MaterialCardView
    private lateinit var categoryCard: com.google.android.material.card.MaterialCardView
    private lateinit var tvCategoryHeader: TextView
    private lateinit var tvTripsHeader: TextView

    private lateinit var tvBudgetTotalRemaining: TextView
    private lateinit var tvBudgetTotal: TextView
    private lateinit var tvBudgetTotalSpent: TextView

    private lateinit var recyclerCategories: RecyclerView
    private lateinit var recyclerBudgetTrips: RecyclerView

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val TAG = "BudgetActivity"
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        bottomNav = findViewById(R.id.bottomNav)
        progressBudget = findViewById(R.id.progressBudget)
        emptyBudgetState = findViewById(R.id.emptyBudgetState)
        totalSummaryCard = findViewById(R.id.totalSummaryCard)
        categoryCard = findViewById(R.id.categoryCard)
        tvCategoryHeader = findViewById(R.id.tvCategoryHeader)
        tvTripsHeader = findViewById(R.id.tvTripsHeader)

        tvBudgetTotalRemaining = findViewById(R.id.tvBudgetTotalRemaining)
        tvBudgetTotal = findViewById(R.id.tvBudgetTotal)
        tvBudgetTotalSpent = findViewById(R.id.tvBudgetTotalSpent)

        recyclerCategories = findViewById(R.id.recyclerCategories)
        recyclerBudgetTrips = findViewById(R.id.recyclerBudgetTrips)

        recyclerCategories.layoutManager = LinearLayoutManager(this)
        recyclerBudgetTrips.layoutManager = LinearLayoutManager(this)

        NavigationHelper.setupBottomNavigation(this, bottomNav, R.id.nav_budget)
        NavigationHelper.setupCommonActions(this)
    }

    override fun onResume() {
        super.onResume()
        loadBudgetData()
    }

    private fun loadBudgetData() {
        val userId = auth.currentUser?.uid ?: return

        progressBudget.visibility = View.VISIBLE
        emptyBudgetState.visibility = View.GONE
        totalSummaryCard.visibility = View.GONE
        categoryCard.visibility = View.GONE
        tvCategoryHeader.visibility = View.GONE
        tvTripsHeader.visibility = View.GONE
        recyclerBudgetTrips.visibility = View.GONE

        db.collection("users").document(userId)
            .collection("trips")
            .get()
            .addOnSuccessListener { snapshot ->
                val trips = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Trip::class.java)?.copy(tripId = doc.id)
                }

                if (trips.isEmpty()) {
                    progressBudget.visibility = View.GONE
                    emptyBudgetState.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                // === TOTALS ===
                var totalBudget = 0.0
                var totalSpent = 0.0
                trips.forEach { trip ->
                    totalBudget += trip.totalBudget
                    totalSpent += (trip.totalBudget - trip.remainingBudget)
                }
                val totalRemaining = totalBudget - totalSpent

                tvBudgetTotalRemaining.text = "R%,.2f".format(totalRemaining)
                tvBudgetTotal.text = "R%,.2f".format(totalBudget)
                tvBudgetTotalSpent.text = "R%,.2f".format(totalSpent)

                // === TRIPS LIST ===
                recyclerBudgetTrips.adapter = BudgetTripAdapter(trips) { trip ->
                    val intent = Intent(this, TripDetailActivity::class.java)
                    intent.putExtra(TripDetailActivity.EXTRA_TRIP_ID, trip.tripId)
                    intent.putExtra(TripDetailActivity.EXTRA_DESTINATION, trip.destination)
                    intent.putExtra(TripDetailActivity.EXTRA_START_DATE,
                        trip.startDate?.seconds?.times(1000) ?: 0L)
                    intent.putExtra(TripDetailActivity.EXTRA_END_DATE,
                        trip.endDate?.seconds?.times(1000) ?: 0L)
                    intent.putExtra(TripDetailActivity.EXTRA_TOTAL_BUDGET, trip.totalBudget)
                    intent.putExtra(TripDetailActivity.EXTRA_REMAINING_BUDGET, trip.remainingBudget)
                    intent.putExtra(TripDetailActivity.EXTRA_DAILY_BURN, trip.dailyBurnRate)
                    startActivity(intent)
                }

                // === CATEGORY BREAKDOWN ===
                loadCategoryTotals(userId, trips, totalSpent)
            }
            .addOnFailureListener { e ->
                progressBudget.visibility = View.GONE
                emptyBudgetState.visibility = View.VISIBLE
                Log.w(TAG, "Failed to load trips", e)
            }
    }

    /**
     * Loads all expenses across all trips and aggregates them by category.
     */
    private fun loadCategoryTotals(
        userId: String,
        trips: List<Trip>,
        totalSpent: Double
    ) {
        val categoryTotals = mutableMapOf<String, Double>()
        var completed = 0

        trips.forEach { trip ->
            db.collection("users").document(userId)
                .collection("trips").document(trip.tripId)
                .collection("expenses")
                .get()
                .addOnSuccessListener { expSnapshot ->
                    expSnapshot.documents.forEach { doc ->
                        val expense = doc.toObject(Expense::class.java)
                        if (expense != null) {
                            categoryTotals[expense.category] =
                                (categoryTotals[expense.category] ?: 0.0) + expense.costInZAR
                        }
                    }
                    completed++
                    if (completed == trips.size) {
                        showCategoryBreakdown(categoryTotals, totalSpent, trips)
                    }
                }
                .addOnFailureListener {
                    completed++
                    if (completed == trips.size) {
                        showCategoryBreakdown(categoryTotals, totalSpent, trips)
                    }
                }
        }
    }

    private fun showCategoryBreakdown(
        categoryTotals: Map<String, Double>,
        totalSpent: Double,
        trips: List<Trip>
    ) {
        progressBudget.visibility = View.GONE

        // Convert the Map<String, Double> to a List<CategoryTotal> explicitly
        val categoryItems = categoryTotals
            .filter { it.value > 0 }
            .map { entry ->
                val pct = if (totalSpent > 0) (entry.value / totalSpent) * 100 else 0.0
                CategoryTotal(entry.key, entry.value, pct)
            }
            .sortedByDescending { it.amount }

        if (categoryItems.isEmpty()) {
            totalSummaryCard.visibility = View.VISIBLE
            categoryCard.visibility = View.GONE
            tvCategoryHeader.visibility = View.GONE
            tvTripsHeader.visibility = View.VISIBLE
            recyclerBudgetTrips.visibility = View.VISIBLE
            return
        }

        totalSummaryCard.visibility = View.VISIBLE
        categoryCard.visibility = View.VISIBLE
        tvCategoryHeader.visibility = View.VISIBLE
        tvTripsHeader.visibility = View.VISIBLE
        recyclerBudgetTrips.visibility = View.VISIBLE

        recyclerCategories.adapter = BudgetCategoryAdapter(categoryItems)
    }
}