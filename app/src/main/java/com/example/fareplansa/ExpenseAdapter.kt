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

        holder.tvVendor.text = expense.vendor.ifBlank { "Custom Expense" }
        holder.tvDescription.text = expense.description.ifBlank { "(no description)" }
        holder.tvCategory.text = expense.category
        holder.tvCost.text = "-R%,.2f".format(expense.costInZAR)
    }

    override fun getItemCount(): Int = expenses.size
}