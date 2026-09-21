package com.example.fareplansa

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var tvSearchBudget: TextView
    private lateinit var radioType: RadioGroup
    private lateinit var radioFlights: RadioButton
    private lateinit var radioHotels: RadioButton
    private lateinit var etSearchQuery: EditText
    private lateinit var btnSearch: ImageView
    private lateinit var btnBack: ImageView
    private lateinit var progressSearch: ProgressBar
    private lateinit var tvSearchEmpty: TextView
    private lateinit var recyclerResults: RecyclerView
    private lateinit var tvResultCount: TextView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNav: BottomNavigationView

    private val repo = SearchRepository()
    private var remainingBudget: Double = 0.0
    private var tripId: String = ""

    companion object {
        const val EXTRA_TRIP_ID = "extra_trip_id"
        const val EXTRA_REMAINING_BUDGET = "extra_remaining_budget"
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Initialize views
        drawerLayout = findViewById(R.id.drawerLayout)
        tvSearchBudget = findViewById(R.id.tvSearchBudget)
        radioType = findViewById(R.id.radioType)
        radioFlights = findViewById(R.id.radioFlights)
        radioHotels = findViewById(R.id.radioHotels)
        etSearchQuery = findViewById(R.id.etSearchQuery)
        btnSearch = findViewById(R.id.btnSearch)
        btnBack = findViewById(R.id.btnBack)
        progressSearch = findViewById(R.id.progressSearch)
        tvSearchEmpty = findViewById(R.id.tvSearchEmpty)
        recyclerResults = findViewById(R.id.recyclerResults)
        tvResultCount = findViewById(R.id.tvResultCount)
        bottomNav = findViewById(R.id.bottomNav)

        recyclerResults.layoutManager = LinearLayoutManager(this)

        // Get extras
        tripId = intent.getStringExtra(EXTRA_TRIP_ID) ?: ""
        remainingBudget = intent.getDoubleExtra(EXTRA_REMAINING_BUDGET, 0.0)

        // Setup UI
        tvSearchBudget.text = getString(R.string.search_budget_cap, remainingBudget)
        tvResultCount.text = "0 OPTIONS FOUND"

        // Setup Navigation
        NavigationHelper.setupBottomNavigation(this, bottomNav, -1)
        NavigationHelper.setupDrawer(this, drawerLayout, findViewById(R.id.btnMenu))
        NavigationHelper.setupCommonActions(this)

        // Listeners
        btnSearch.setOnClickListener { performSearch() }
        btnBack.setOnClickListener { finish() }

        etSearchQuery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else false
        }
    }

    private fun performSearch() {
        val query = etSearchQuery.text.toString().trim()
        if (query.isEmpty()) {
            Toast.makeText(this, "Enter a destination", Toast.LENGTH_SHORT).show()
            return
        }

        val type = if (radioFlights.isChecked) "flight" else "hotel"

        // Split budget: assume flights ≈ 30% of remaining, hotels ≈ 40% per night
        val maxPrice = when (type) {
            "flight" -> remainingBudget * 0.30
            "hotel" -> remainingBudget * 0.40
            else -> remainingBudget
        }

        progressSearch.visibility = View.VISIBLE
        tvSearchEmpty.visibility = View.GONE
        recyclerResults.adapter = null
        tvResultCount.text = "SEARCHING..."

        lifecycleScope.launch {
            try {
                val results = repo.search(type, query, maxPrice)

                progressSearch.visibility = View.GONE

                if (results.isEmpty()) {
                    tvSearchEmpty.text = getString(R.string.search_no_results, query)
                    tvSearchEmpty.visibility = View.VISIBLE
                    tvResultCount.text = "0 OPTIONS FOUND"
                } else {
                    tvSearchEmpty.visibility = View.GONE
                    tvResultCount.text = "${results.size} OPTIONS FOUND"
                    recyclerResults.adapter = SearchResultAdapter(results) { result ->
                        openBooking(result)
                    }
                }
            } catch (e: Exception) {
                progressSearch.visibility = View.GONE
                tvSearchEmpty.text = "Search failed: ${e.message}"
                tvSearchEmpty.visibility = View.VISIBLE
                tvResultCount.text = "ERROR"
            }
        }
    }

    private fun openBooking(result: SearchResult) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.deepLink))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not open booking link", Toast.LENGTH_SHORT).show()
        }
    }
}