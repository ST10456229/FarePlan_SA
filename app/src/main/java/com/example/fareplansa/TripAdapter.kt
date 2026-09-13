package com.example.fareplansa

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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

        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val start = trip.startDate?.toDate()?.let { formatter.format(it) } ?: "?"
        val end = trip.endDate?.toDate()?.let { formatter.format(it) } ?: "?"
        holder.tvDates.text = "$start - $end"

        holder.tvBudget.text = "Budget: R%,.2f | Remaining: R%,.2f"
            .format(trip.totalBudget, trip.remainingBudget)

        val percent = BudgetAlertHelper.spentPercent(trip)
        holder.progressBudget.progress = percent
        holder.progressBudget.progressTintList =
            ColorStateList.valueOf(BudgetAlertHelper.progressColor(trip))

        holder.itemView.setOnClickListener { onTripClick(trip) }
    }

    override fun getItemCount(): Int = trips.size
}