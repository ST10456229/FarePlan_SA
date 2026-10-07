package com.example.fareplansa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale

class LiveActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var progressLive: ProgressBar
    private lateinit var emptyLiveState: LinearLayout
    private lateinit var activeTripCard: com.google.android.material.card.MaterialCardView
    private lateinit var tvLiveDestination: TextView
    private lateinit var tvLiveDates: TextView
    private lateinit var tvLiveDaysRemaining: TextView
    private lateinit var tvLiveRemainingBudget: TextView
    private lateinit var btnLiveAddExpense: MaterialButton
    private lateinit var tvLiveTodayHeader: TextView
    private lateinit var liveItineraryContainer: LinearLayout

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var activeTripId: String = ""

    companion object {
        private const val TAG = "LiveActivity"
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live)

        bottomNav = findViewById(R.id.bottomNav)
        progressLive = findViewById(R.id.progressLive)
        emptyLiveState = findViewById(R.id.emptyLiveState)
        activeTripCard = findViewById(R.id.activeTripCard)
        tvLiveDestination = findViewById(R.id.tvLiveDestination)
        tvLiveDates = findViewById(R.id.tvLiveDates)
        tvLiveDaysRemaining = findViewById(R.id.tvLiveDaysRemaining)
        tvLiveRemainingBudget = findViewById(R.id.tvLiveRemainingBudget)
        btnLiveAddExpense = findViewById(R.id.btnLiveAddExpense)
        tvLiveTodayHeader = findViewById(R.id.tvLiveTodayHeader)
        liveItineraryContainer = findViewById(R.id.liveItineraryContainer)

        NavigationHelper.setupBottomNavigation(this, bottomNav, R.id.nav_live)
        NavigationHelper.setupCommonActions(this)

        btnLiveAddExpense.setOnClickListener {
            if (activeTripId.isNotEmpty()) {
                val intent = Intent(this, AddExpenseActivity::class.java)
                intent.putExtra(AddExpenseActivity.EXTRA_TRIP_ID, activeTripId)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        findActiveTrip()
    }

    /**
     * Finds the trip whose date range includes today.
     * If no active trip is found, falls back to the most recent trip.
     */
    private fun findActiveTrip() {
        val userId = auth.currentUser?.uid ?: return

        progressLive.visibility = View.VISIBLE
        emptyLiveState.visibility = View.GONE
        activeTripCard.visibility = View.GONE
        tvLiveTodayHeader.visibility = View.GONE
        liveItineraryContainer.visibility = View.GONE

        db.collection("users").document(userId)
            .collection("trips")
            .orderBy("startDate", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                progressLive.visibility = View.GONE

                val trips = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Trip::class.java)?.copy(tripId = doc.id)
                }

                if (trips.isEmpty()) {
                    emptyLiveState.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                // Find current trip (today within range)
                val now = System.currentTimeMillis() / 1000
                val currentTrip = trips.firstOrNull { trip ->
                    val start = trip.startDate?.seconds ?: 0
                    val end = trip.endDate?.seconds ?: 0
                    now in start..end
                }

                // Fallback to most recent trip if none active
                val displayTrip = currentTrip ?: trips.first()

                activeTripId = displayTrip.tripId
                showTrip(displayTrip, isActive = currentTrip != null)
            }
            .addOnFailureListener { e ->
                progressLive.visibility = View.GONE
                emptyLiveState.visibility = View.VISIBLE
                Log.w(TAG, "Failed to load trips", e)
            }
    }

    private fun showTrip(trip: Trip, isActive: Boolean) {
        activeTripCard.visibility = View.VISIBLE

        tvLiveDestination.text = trip.destination

        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val start = trip.startDate?.toDate()?.let { formatter.format(it) } ?: "?"
        val end = trip.endDate?.toDate()?.let { formatter.format(it) } ?: "?"
        tvLiveDates.text = "$start - $end"

        // Days remaining
        val nowMillis = System.currentTimeMillis()
        val endMillis = trip.endDate?.seconds?.times(1000) ?: 0L
        val startMillis = trip.startDate?.seconds?.times(1000) ?: 0L

        val daysRemainingText = when {
            !isActive && nowMillis < startMillis -> {
                val daysUntilStart = ((startMillis - nowMillis) / (1000 * 60 * 60 * 24)).toInt()
                getString(R.string.live_starts_in_days, daysUntilStart)
            }
            isActive -> {
                val daysLeft = ((endMillis - nowMillis) / (1000 * 60 * 60 * 24)).toInt()
                if (daysLeft < 0) {
                    getString(R.string.live_trip_complete)
                } else {
                    getString(R.string.live_days_remaining, daysLeft)
                }
            }
            else -> getString(R.string.live_trip_complete)
        }
        tvLiveDaysRemaining.text = daysRemainingText

        // Remaining budget
        tvLiveRemainingBudget.text = getString(R.string.trip_detail_remaining, trip.remainingBudget)

        // Load today's itinerary items
        loadTodayItinerary(trip.tripId)
    }

    private fun loadTodayItinerary(tripId: String) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("trips").document(tripId)
            .collection("itinerary")
            .orderBy("date", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val items = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(ItineraryItem::class.java)?.copy(itemId = doc.id)
                }

                if (items.isEmpty()) {
                    tvLiveTodayHeader.visibility = View.GONE
                    liveItineraryContainer.visibility = View.GONE
                    return@addOnSuccessListener
                }

                tvLiveTodayHeader.visibility = View.VISIBLE
                liveItineraryContainer.visibility = View.VISIBLE
                liveItineraryContainer.removeAllViews()

                // Show up to 5 items
                items.take(5).forEach { item ->
                    val row = layoutInflater.inflate(R.layout.item_live_itinerary, liveItineraryContainer, false)
                    row.findViewById<TextView>(R.id.tvLiveItemTitle).text = item.title
                    row.findViewById<TextView>(R.id.tvLiveItemTime).text =
                        if (item.time.isNotBlank()) item.time else "—"
                    row.findViewById<TextView>(R.id.tvLiveItemStatus).text =
                        if (item.isDone) getString(R.string.live_done) else getString(R.string.live_pending)

                    liveItineraryContainer.addView(row)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Failed to load itinerary", e)
            }
    }
}