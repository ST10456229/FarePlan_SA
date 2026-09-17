package com.example.fareplansa

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale
import android.content.Context

class TripDetailActivity : AppCompatActivity() {

    private lateinit var tvDestination: TextView
    private lateinit var tvDates: TextView
    private lateinit var tvRemaining: TextView
    private lateinit var tvBurnRate: TextView
    private lateinit var tvBudgetStatus: TextView
    private lateinit var progressBudget: ProgressBar
    private lateinit var btnAddExpense: Button
    private lateinit var recyclerExpenses: RecyclerView
    private lateinit var tvExpenseEmptyState: TextView

    private lateinit var btnSearch: Button

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
        recyclerExpenses = findViewById(R.id.recyclerExpenses)
        tvExpenseEmptyState = findViewById(R.id.tvExpenseEmptyState)
        btnSearch = findViewById(R.id.btnSearch)

        recyclerExpenses.layoutManager = LinearLayoutManager(this)

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

// ...

// In the click listener section:
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
    }

    private fun renderTripHeader() {
        val t = trip ?: return

        tvDestination.text = t.destination

        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val start = t.startDate?.toDate()?.let { formatter.format(it) } ?: "?"
        val end = t.endDate?.toDate()?.let { formatter.format(it) } ?: "?"
        tvDates.text = "$start - $end"

        tvRemaining.text = "R%,.2f remaining".format(t.remainingBudget)
        tvBurnRate.text = "Daily budget: R%,.2f".format(t.dailyBurnRate)

        // Budget progress via helper
        val percent = BudgetAlertHelper.spentPercent(t)
        progressBudget.progress = percent
        progressBudget.progressTintList =
            ColorStateList.valueOf(BudgetAlertHelper.progressColor(t))

        // Status banner
        val message = BudgetAlertHelper.statusMessage(t)
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
}