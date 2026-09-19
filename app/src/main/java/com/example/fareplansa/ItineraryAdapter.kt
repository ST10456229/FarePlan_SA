package com.example.fareplansa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class ItineraryAdapter(
    private val items: List<ItineraryItem>,
    private val onToggleDone: (ItineraryItem, Boolean) -> Unit,
    private val onEditClick: (ItineraryItem) -> Unit,
    private val onDeleteClick: (ItineraryItem) -> Unit
) : RecyclerView.Adapter<ItineraryAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cbDone: CheckBox = view.findViewById(R.id.cbItineraryDone)
        val tvTitle: TextView = view.findViewById(R.id.tvItineraryTitle)
        val tvNotes: TextView = view.findViewById(R.id.tvItineraryNotes)
        val tvDateTime: TextView = view.findViewById(R.id.tvItineraryDateTime)
        val btnEdit: ImageButton = view.findViewById(R.id.btnItineraryEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnItineraryDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_itinerary, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        holder.tvTitle.text = item.title
        holder.tvNotes.text = item.notes
        holder.tvNotes.visibility = if (item.notes.isBlank()) View.GONE else View.VISIBLE

        // Format date + time
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateStr = item.date?.toDate()?.let { formatter.format(it) } ?: ""
        val timeStr = if (item.time.isBlank()) "" else " · ${item.time}"
        holder.tvDateTime.text = "$dateStr$timeStr"

        // Set checkbox without triggering the listener during binding
        holder.cbDone.setOnCheckedChangeListener(null)
        holder.cbDone.isChecked = item.isDone
        holder.cbDone.setOnCheckedChangeListener { _, isChecked ->
            onToggleDone(item, isChecked)
        }

        // Strikethrough style when done
        if (item.isDone) {
            holder.tvTitle.paintFlags = holder.tvTitle.paintFlags or
                    android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvTitle.alpha = 0.5f
        } else {
            holder.tvTitle.paintFlags = holder.tvTitle.paintFlags and
                    android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.tvTitle.alpha = 1.0f
        }

        holder.btnEdit.setOnClickListener { onEditClick(item) }
        holder.btnDelete.setOnClickListener { onDeleteClick(item) }
    }

    override fun getItemCount(): Int = items.size
}