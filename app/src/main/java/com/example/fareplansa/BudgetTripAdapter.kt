package com.example.fareplansa

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BudgetTripAdapter(
    private val trips: List<Trip>,
    private val onTripClick: (Trip) -> Unit
) : RecyclerView.Adapter<BudgetTripAdapter.BudgetTripViewHolder>() {

    class BudgetTripViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDestination: TextView = view.findViewById(R.id.tvBudgetTripDestination)
        val tvPercent: TextView = view.findViewById(R.id.tvBudgetTripPercent)
        val tvAmount: TextView = view.findViewById(R.id.tvBudgetTripAmount)
        val progress: ProgressBar = view.findViewById(R.id.progressBudgetTrip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetTripViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_budget_trip, parent, false)
        return BudgetTripViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetTripViewHolder, position: Int) {
        val trip = trips[position]

        holder.tvDestination.text = trip.destination

        val percent = BudgetAlertHelper.spentPercent(trip)
        val spent = trip.totalBudget - trip.remainingBudget

        holder.tvPercent.text = "$percent%"
        holder.tvAmount.text = "R%,.2f of R%,.2f".format(spent, trip.totalBudget)

        holder.progress.progress = percent
        holder.progress.progressTintList =
            ColorStateList.valueOf(BudgetAlertHelper.progressColor(trip))

        holder.itemView.setOnClickListener { onTripClick(trip) }
    }

    override fun getItemCount(): Int = trips.size
}