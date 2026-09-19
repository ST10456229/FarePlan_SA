package com.example.fareplansa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(
    private val expenses: List<Expense>
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvVendor: TextView = view.findViewById(R.id.tvExpenseVendor)
        val tvDescription: TextView = view.findViewById(R.id.tvExpenseDescription)
        val tvCategory: TextView = view.findViewById(R.id.tvExpenseCategory)
        val tvCost: TextView = view.findViewById(R.id.tvExpenseCost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        val context = holder.itemView.context

        holder.tvVendor.text = expense.vendor.ifBlank { 
            context.getString(R.string.expense_item_custom) 
        }
        holder.tvDescription.text = expense.description.ifBlank { 
            context.getString(R.string.expense_item_no_desc) 
        }
        
        // Translate category from stored key
        holder.tvCategory.text = when (expense.category) {
            "Flight" -> context.getString(R.string.category_flight)
            "Hotel/Airbnb" -> context.getString(R.string.category_hotel)
            "Car" -> context.getString(R.string.category_car)
            "Food" -> context.getString(R.string.category_food)
            "Activity" -> context.getString(R.string.category_activity)
            else -> context.getString(R.string.category_custom)
        }

        holder.tvCost.text = context.getString(R.string.expense_item_cost_format, expense.costInZAR)
    }

    override fun getItemCount(): Int = expenses.size
}