package com.example.fareplansa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var tvSearchBudget: TextView
    private lateinit var radioType: RadioGroup
    private lateinit var radioFlights: RadioButton
    private lateinit var radioHotels: RadioButton
    private lateinit var etSearchQuery: EditText
    private lateinit var btnSearch: ImageView
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

        // Default to Flights selected
        radioFlights.isChecked = true

        tripId = intent.getStringExtra(EXTRA_TRIP_ID) ?: ""
        remainingBudget = intent.getDoubleExtra(EXTRA_REMAINING_BUDGET, 0.0)

        tvSearchBudget.text = getString(R.string.search_budget_cap, remainingBudget)

        // Tap the search icon button
        btnSearch.setOnClickListener { performSearch() }

        // Press the search key on the keyboard (magnifying glass icon)
        etSearchQuery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                hideKeyboard()
                true
            } else {
                false
            }
        }
    }

    /**
     * Hides the on-screen keyboard after a search is triggered.
     */
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etSearchQuery.windowToken, 0)
    }

    private fun performSearch() {
        val query = etSearchQuery.text.toString().trim()
        if (query.isEmpty()) {
            Toast.makeText(this, getString(R.string.search_empty), Toast.LENGTH_SHORT).show()
            return
        }

        val type = if (radioFlights.isChecked) "flight" else "hotel"
        val maxPrice = remainingBudget

        progressSearch.visibility = View.VISIBLE
        tvSearchEmpty.visibility = View.GONE
        recyclerResults.adapter = null

        lifecycleScope.launch {
            try {
                val results = repo.search(type, query, maxPrice)
                progressSearch.visibility = View.GONE

                if (results.isEmpty()) {
                    tvSearchEmpty.text = getString(R.string.search_no_results, query)
                    tvSearchEmpty.visibility = View.VISIBLE
                } else {
                    tvSearchEmpty.visibility = View.GONE
                    recyclerResults.adapter = SearchResultAdapter(results) { result ->
                        onBookClicked(result)
                    }
                }
            } catch (e: Exception) {
                progressSearch.visibility = View.GONE
                tvSearchEmpty.text = "Search failed: ${e.message}"
                tvSearchEmpty.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Called when the user taps "Book" on a search result.
     * Instead of opening a browser, we save the booking into the trip.
     */
    private fun onBookClicked(result: SearchResult) {
        val intent = Intent(this, AddExpenseActivity::class.java).apply {
            putExtra(AddExpenseActivity.EXTRA_TRIP_ID, tripId)

            val category = if (result.type == "flight") "Flight" else "Hotel/Airbnb"
            putExtra(AddExpenseActivity.EXTRA_PREFILL_CATEGORY, category)
            putExtra(AddExpenseActivity.EXTRA_PREFILL_VENDOR, result.provider.ifBlank { result.title })
            putExtra(AddExpenseActivity.EXTRA_PREFILL_DESCRIPTION, result.title)
            putExtra(AddExpenseActivity.EXTRA_PREFILL_COST, result.priceInZAR)
            putExtra(AddExpenseActivity.EXTRA_PREFILL_DATE_MILLIS, System.currentTimeMillis())
            putExtra(AddExpenseActivity.EXTRA_ADD_TO_ITINERARY, true)
        }
        startActivity(intent)
    }
}