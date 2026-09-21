package com.example.fareplansa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SearchResultAdapter(
    private val results: List<SearchResult>,
    private val onBookClick: (SearchResult) -> Unit
) : RecyclerView.Adapter<SearchResultAdapter.ResultViewHolder>() {

    class ResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvResultTitle)
        val tvSubtitle: TextView = view.findViewById(R.id.tvResultSubtitle)
        val tvProvider: TextView = view.findViewById(R.id.tvResultProvider)
        val tvPrice: TextView = view.findViewById(R.id.tvResultPrice)
        val tvBadge: TextView = view.findViewById(R.id.tvResultBadge)
        val btnBook: Button = view.findViewById(R.id.btnResultBook)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val result = results[position]
        val context = holder.itemView.context

        holder.tvTitle.text = result.title
        holder.tvSubtitle.text = result.subtitle
        holder.tvProvider.text = context.getString(R.string.search_result_via, result.provider)
        holder.tvPrice.text = context.getString(R.string.search_result_price, result.priceInZAR)
        holder.btnBook.setOnClickListener { onBookClick(result) }

        // All results from the repository are pre-filtered by budget.
        holder.tvBadge.text = "IN BUDGET"
        holder.tvBadge.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int = results.size
}