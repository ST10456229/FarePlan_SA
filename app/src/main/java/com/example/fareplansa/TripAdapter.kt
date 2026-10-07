package com.example.fareplansa

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class TripAdapter(
    private val trips: List<Trip>,
    private val onDeleteClick: ((Trip) -> Unit)? = null,
    private val onTripClick: (Trip) -> Unit
) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    class TripViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDestination: TextView = view.findViewById(R.id.tvTripDestination)
        val tvDates: TextView = view.findViewById(R.id.tvTripDates)
        val tvBudget: TextView = view.findViewById(R.id.tvTripBudget)
        val tvUtilized: TextView = view.findViewById(R.id.tvTripUtilized)
        val progressBudget: ProgressBar = view.findViewById(R.id.progressBudget)
        val btnDeleteTrip: ImageView = view.findViewById(R.id.btnDeleteTrip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trip, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]
        val context = holder.itemView.context
        val locale = context.resources.configuration.locales[0]

        holder.tvDestination.text = trip.destination

        val formatter = SimpleDateFormat("dd/MM/yyyy", locale)
        val unknown = context.getString(R.string.trip_item_unknown_date)
        val start = trip.startDate?.toDate()?.let { formatter.format(it) } ?: unknown
        val end = trip.endDate?.toDate()?.let { formatter.format(it) } ?: unknown
        
        holder.tvDates.text = context.getString(R.string.trip_item_date_range, start, end)

        holder.tvBudget.text = context.getString(
            R.string.trip_item_budget_summary,
            trip.totalBudget,
            trip.remainingBudget
        )

        val percent = BudgetAlertHelper.spentPercent(trip)
        holder.progressBudget.progress = percent
        holder.progressBudget.progressTintList =
            ColorStateList.valueOf(BudgetAlertHelper.progressColor(trip))

        holder.tvUtilized.text = "$percent% Utilized"

        if (onDeleteClick != null) {
            holder.btnDeleteTrip.visibility = View.VISIBLE
            holder.btnDeleteTrip.setOnClickListener { onDeleteClick.invoke(trip) }
        } else {
            holder.btnDeleteTrip.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { onTripClick(trip) }
    }

    override fun getItemCount(): Int = trips.size
}