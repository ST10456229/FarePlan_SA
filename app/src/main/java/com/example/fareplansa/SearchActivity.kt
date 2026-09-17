package com.example.fareplansa

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import android.content.Context

class SearchActivity : AppCompatActivity() {

    private lateinit var tvSearchBudget: TextView
    private lateinit var radioType: RadioGroup
    private lateinit var radioFlights: android.widget.RadioButton
    private lateinit var radioHotels: android.widget.RadioButton
    private lateinit var etSearchQuery: EditText
    private lateinit var btnSearch: Button
    private lateinit var progressSearch: ProgressBar
    private lateinit var tvSearchEmpty: TextView
    private lateinit var recyclerResults: RecyclerView

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

        tvSearchBudget = findViewById(R.id.tvSearchBudget)
        radioType = findViewById(R.id.radioType)
        radioFlights = findViewById(R.id.radioFlights)
        radioHotels = findViewById(R.id.radioHotels)
        etSearchQuery = findViewById(R.id.etSearchQuery)
        btnSearch = findViewById(R.id.btnSearch)
        progressSearch = findViewById(R.id.progressSearch)
        tvSearchEmpty = findViewById(R.id.tvSearchEmpty)
        recyclerResults = findViewById(R.id.recyclerResults)

        recyclerResults.layoutManager = LinearLayoutManager(this)

        tripId = intent.getStringExtra(EXTRA_TRIP_ID) ?: ""
        remainingBudget = intent.getDoubleExtra(EXTRA_REMAINING_BUDGET, 0.0)

        tvSearchBudget.text = "Budget cap: R%,.2f".format(remainingBudget)

        btnSearch.setOnClickListener { performSearch() }
    }

    private fun performSearch() {
        val query = etSearchQuery.text.toString().trim()
        if (query.isEmpty()) {
            Toast.makeText(this, "Enter a destination", Toast.LENGTH_SHORT).show()
            return
        }

        val type = if (radioFlights.isChecked) "flight" else "hotel"

        // Split budget: assume flights ≈ 30% of remaining, hotels ≈ 40% per night
        // This is where Dynamic Budget Tethering filters results
        val maxPrice = when (type) {
            "flight" -> remainingBudget * 0.30
            "hotel" -> remainingBudget * 0.40
            else -> remainingBudget
        }

        progressSearch.visibility = View.VISIBLE
        tvSearchEmpty.visibility = View.GONE
        recyclerResults.adapter = null

        lifecycleScope.launch {
            try {
                val results = repo.search(type, query, maxPrice)

                progressSearch.visibility = View.GONE

                if (results.isEmpty()) {
                    tvSearchEmpty.text = "No options within your budget for \"$query\""
                    tvSearchEmpty.visibility = View.VISIBLE
                } else {
                    tvSearchEmpty.visibility = View.GONE
                    recyclerResults.adapter = SearchResultAdapter(results) { result ->
                        openBooking(result)
                    }
                }
            } catch (e: Exception) {
                progressSearch.visibility = View.GONE
                tvSearchEmpty.text = "Search failed: ${e.message}"
                tvSearchEmpty.visibility = View.VISIBLE
            }
        }
    }

    private fun openBooking(result: SearchResult) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.deepLink))
        startActivity(intent)
    }
}