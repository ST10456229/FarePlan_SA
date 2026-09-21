package com.example.fareplansa

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class TripDetailActivity : AppCompatActivity() {

    private lateinit var tvDestination: TextView
    private lateinit var tvDates: TextView
    private lateinit var tvRemaining: TextView
    private lateinit var tvBurnRate: TextView
    private lateinit var tvBudgetStatus: TextView
    private lateinit var progressBudget: ProgressBar
    private lateinit var btnAddExpense: Button
    private lateinit var btnAddItinerary: Button
    private lateinit var btnSearch: Button
    private lateinit var recyclerExpenses: RecyclerView
    private lateinit var recyclerItinerary: RecyclerView
    private lateinit var tvExpenseEmptyState: TextView
    private lateinit var tvItineraryEmpty: TextView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNav: BottomNavigationView

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var tripId: String = ""
    private var trip: Trip? = null

    companion object {
        private const val TAG = "TripDetailActivity"
        const val EXTRA_TRIP_ID = "extra_trip_id"
        const val EXTRA_DESTINATION = "extra_destination"
        const val EXTRA_START_DATE = "extra_start_date"
        const val EXTRA_END_DATE = "extra_end_date"
        const val EXTRA_TOTAL_BUDGET = "extra_total_budget"
        const val EXTRA_REMAINING_BUDGET = "extra_remaining_budget"
        const val EXTRA_DAILY_BURN = "extra_daily_burn"
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_detail)

        tvDestination = findViewById(R.id.tvDetailDestination)
        tvDates = findViewById(R.id.tvDetailDates)
        tvRemaining = findViewById(R.id.tvDetailRemaining)
        tvBurnRate = findViewById(R.id.tvDetailBurnRate)
        tvBudgetStatus = findViewById(R.id.tvBudgetStatus)
        progressBudget = findViewById(R.id.progressDetailBudget)
        btnAddExpense = findViewById(R.id.btnAddExpense)
        btnAddItinerary = findViewById(R.id.btnAddItinerary)
        btnSearch = findViewById(R.id.btnSearch)
        recyclerExpenses = findViewById(R.id.recyclerExpenses)
        recyclerItinerary = findViewById(R.id.recyclerItinerary)
        tvExpenseEmptyState = findViewById(R.id.tvExpenseEmptyState)
        tvItineraryEmpty = findViewById(R.id.tvItineraryEmpty)
        drawerLayout = findViewById(R.id.drawerLayout)
        bottomNav = findViewById(R.id.bottomNav)

        recyclerExpenses.layoutManager = LinearLayoutManager(this)
        recyclerItinerary.layoutManager = LinearLayoutManager(this)

        // Setup Navigation - TripDetail doesn't have a specific bottom nav item highlighted usually, 
        // but we'll leave it unselected or highlight Trips if it came from there.
        NavigationHelper.setupBottomNavigation(this, bottomNav, -1) 
        NavigationHelper.setupDrawer(this, drawerLayout, findViewById(R.id.btnMenu))
        NavigationHelper.setupCommonActions(this)

        tripId = intent.getStringExtra(EXTRA_TRIP_ID) ?: ""
        val destination = intent.getStringExtra(EXTRA_DESTINATION) ?: "Trip"
        val startMillis = intent.getLongExtra(EXTRA_START_DATE, 0L)
        val endMillis = intent.getLongExtra(EXTRA_END_DATE, 0L)
        val totalBudget = intent.getDoubleExtra(EXTRA_TOTAL_BUDGET, 0.0)
        val remainingBudget = intent.getDoubleExtra(EXTRA_REMAINING_BUDGET, 0.0)
        val dailyBurn = intent.getDoubleExtra(EXTRA_DAILY_BURN, 0.0)

        trip = Trip(
            tripId = tripId,
            destination = destination,
            startDate = if (startMillis > 0) Timestamp(startMillis / 1000, 0) else null,
            endDate = if (endMillis > 0) Timestamp(endMillis / 1000, 0) else null,
            totalBudget = totalBudget,
            remainingBudget = remainingBudget,
            dailyBurnRate = dailyBurn
        )

        renderTripHeader()

        btnAddExpense.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            intent.putExtra(AddExpenseActivity.EXTRA_TRIP_ID, tripId)
            startActivity(intent)
        }

        btnAddItinerary.setOnClickListener {
            val intent = Intent(this, AddItineraryItemActivity::class.java)
            intent.putExtra(AddItineraryItemActivity.EXTRA_TRIP_ID, tripId)
            startActivity(intent)
        }

        btnSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra(SearchActivity.EXTRA_TRIP_ID, tripId)
            intent.putExtra(SearchActivity.EXTRA_REMAINING_BUDGET, trip?.remainingBudget ?: 0.0)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadTripFromFirestore()
        loadExpenses()
        loadItinerary()
    }

    private fun renderTripHeader() {
        val t = trip ?: return

        tvDestination.text = t.destination

        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val start = t.startDate?.toDate()?.let { formatter.format(it) } ?: "?"
        val end = t.endDate?.toDate()?.let { formatter.format(it) } ?: "?"
        tvDates.text = "$start - $end"

        tvRemaining.text = getString(R.string.trip_detail_remaining, t.remainingBudget)
        tvBurnRate.text = getString(R.string.trip_detail_daily_budget, t.dailyBurnRate)

        val percent = BudgetAlertHelper.spentPercent(t)
        progressBudget.progress = percent
        progressBudget.progressTintList =
            ColorStateList.valueOf(BudgetAlertHelper.progressColor(t))

        val message = BudgetAlertHelper.statusMessage(this, t)
        if (message == null) {
            tvBudgetStatus.visibility = View.GONE
        } else {
            tvBudgetStatus.visibility = View.VISIBLE
            tvBudgetStatus.text = message
            tvBudgetStatus.setBackgroundColor(BudgetAlertHelper.progressColor(t))
        }
    }

    private fun loadTripFromFirestore() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("trips").document(tripId)
            .get()
            .addOnSuccessListener { doc ->
                val updated = doc.toObject(Trip::class.java)
                if (updated != null) {
                    trip = updated.copy(tripId = doc.id)
                    renderTripHeader()
                }
            }
            .addOnFailureListener { e -> Log.w(TAG, "Failed to reload trip", e) }
    }

    private fun loadExpenses() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("trips").document(tripId)
            .collection("expenses")
            .orderBy("date")
            .get()
            .addOnSuccessListener { snapshot ->
                val expenses = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Expense::class.java)?.copy(expenseId = doc.id)
                }

                if (expenses.isEmpty()) {
                    tvExpenseEmptyState.visibility = View.VISIBLE
                    recyclerExpenses.visibility = View.GONE
                } else {
                    tvExpenseEmptyState.visibility = View.GONE
                    recyclerExpenses.visibility = View.VISIBLE
                }

                recyclerExpenses.adapter = ExpenseAdapter(expenses)
            }
            .addOnFailureListener { e -> Log.w(TAG, "Failed to load expenses", e) }
    }

    private fun loadItinerary() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("trips").document(tripId)
            .collection("itinerary")
            .orderBy("date")
            .get()
            .addOnSuccessListener { snapshot ->
                val items = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(ItineraryItem::class.java)?.copy(itemId = doc.id)
                }

                if (items.isEmpty()) {
                    tvItineraryEmpty.visibility = View.VISIBLE
                    recyclerItinerary.visibility = View.GONE
                } else {
                    tvItineraryEmpty.visibility = View.GONE
                    recyclerItinerary.visibility = View.VISIBLE
                }

                recyclerItinerary.adapter = ItineraryAdapter(
                    items = items,
                    onToggleDone = { item, isDone -> updateItemDone(item, isDone) },
                    onEditClick = { item -> openEditItem(item) },
                    onDeleteClick = { item -> deleteItem(item) }
                )
            }
            .addOnFailureListener { e -> Log.w(TAG, "Failed to load itinerary", e) }
    }

    private fun updateItemDone(item: ItineraryItem, isDone: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId)
            .collection("trips").document(tripId)
            .collection("itinerary").document(item.itemId)
            .update("isDone", isDone)
            .addOnFailureListener { e -> Log.w(TAG, "Failed to toggle done", e) }
    }

    private fun openEditItem(item: ItineraryItem) {
        val intent = Intent(this, AddItineraryItemActivity::class.java)
        intent.putExtra(AddItineraryItemActivity.EXTRA_TRIP_ID, tripId)
        intent.putExtra(AddItineraryItemActivity.EXTRA_ITEM_ID, item.itemId)
        intent.putExtra(AddItineraryItemActivity.EXTRA_TITLE, item.title)
        intent.putExtra(AddItineraryItemActivity.EXTRA_NOTES, item.notes)
        intent.putExtra(AddItineraryItemActivity.EXTRA_DATE_MILLIS, item.date?.seconds?.times(1000) ?: 0L)
        intent.putExtra(AddItineraryItemActivity.EXTRA_TIME, item.time)
        startActivity(intent)
    }

    private fun deleteItem(item: ItineraryItem) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId)
            .collection("trips").document(tripId)
            .collection("itinerary").document(item.itemId)
            .delete()
            .addOnSuccessListener {
                android.widget.Toast.makeText(this, "Item deleted", android.widget.Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e -> Log.w(TAG, "Failed to delete item", e) }
    }
}