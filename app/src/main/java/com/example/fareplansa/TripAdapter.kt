package com.example.fareplansa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class TripAdapter(
    private val trips: List<Trip>,
    private val onTripClick: (Trip) -> Unit
) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    class TripViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDestination: TextView = view.findViewById(R.id.tvTripDestination)
        val tvDates: TextView = view.findViewById(R.id.tvTripDates)
        val tvBudget: TextView = view.findViewById(R.id.tvTripBudget)
        val progressBudget: ProgressBar = view.findViewById(R.id.progressBudget)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trip, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]

        holder.tvDestination.text = trip.destination

        // Format dates
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val start = trip.startDate?.toDate()?.let { formatter.format(it) } ?: "?"
        val end = trip.endDate?.toDate()?.let { formatter.format(it) } ?: "?"
        holder.tvDates.text = "$start → $end"

        // Budget text
        holder.tvBudget.text = "Budget: R%,.2f | Remaining: R%,.2f"
            .format(trip.totalBudget, trip.remainingBudget)

        // Progress = percentage of budget spent
        val spent = trip.totalBudget - trip.remainingBudget
        val percent = if (trip.totalBudget > 0) {
            ((spent / trip.totalBudget) * 100).toInt().coerceIn(0, 100)
        } else 0

        holder.progressBudget.progress = percent

        // Color the progress bar based on usage
        val color = when {
            percent < 70 -> 0xFF2ECC71.toInt()   // Green
            percent < 90 -> 0xFFF39C12.toInt()   // Amber
            else -> 0xFFE74C3C.toInt()           // Red
        }
        holder.progressBudget.progressTintList =
            android.content.res.ColorStateList.valueOf(color)

        // Click listener
        holder.itemView.setOnClickListener { onTripClick(trip) }
    }

    override fun getItemCount(): Int = trips.size
}