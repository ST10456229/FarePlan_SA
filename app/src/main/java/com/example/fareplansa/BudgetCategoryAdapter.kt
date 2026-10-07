package com.example.fareplansa

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Data holder for a single spending category on the Budget screen.
 * [percentage] is the share of total spending (0–100).
 */
data class CategoryTotal(
    val category: String,
    val amount: Double,
    val percentage: Double
)

/**
 * Adapter that renders the "Spending by Category" list on the Budget screen.
 * Each row shows the category name, its total, its percentage, and a
 * colour-coded progress bar.
 */
class BudgetCategoryAdapter(
    private val items: List<CategoryTotal>
) : RecyclerView.Adapter<BudgetCategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvCategoryName)
        val tvAmount: TextView = view.findViewById(R.id.tvCategoryAmount)
        val tvPercent: TextView = view.findViewById(R.id.tvCategoryPercent)
        val progress: ProgressBar = view.findViewById(R.id.progressCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_budget_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = items[position]

        holder.tvName.text = item.category
        holder.tvAmount.text = "R%,.2f".format(item.amount)
        holder.tvPercent.text = "%.0f%%".format(item.percentage)

        val progressInt = item.percentage.toInt().coerceIn(0, 100)
        holder.progress.progress = progressInt
        holder.progress.progressTintList =
            ColorStateList.valueOf(colorForCategory(item.category))
    }

    /**
     * Assigns a distinct colour to each spending category so the bars are
     * easy to scan visually.
     */
    private fun colorForCategory(category: String): Int {
        return when (category.lowercase()) {
            "flight"       -> 0xFF2B9EB3.toInt()   // teal
            "hotel/airbnb" -> 0xFFE91E63.toInt()   // pink
            "car"          -> 0xFFF39C12.toInt()   // amber
            "food"         -> 0xFFE74C3C.toInt()   // red
            "activity"     -> 0xFF9C27B0.toInt()   // purple
            else           -> 0xFF607D8B.toInt()   // grey (Custom)
        }
    }

    override fun getItemCount(): Int = items.size
}