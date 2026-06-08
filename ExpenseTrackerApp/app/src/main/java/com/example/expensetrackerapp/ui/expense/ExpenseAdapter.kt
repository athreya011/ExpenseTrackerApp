package com.example.expensetrackerapp.ui.expense

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.R
import com.example.expensetrackerapp.databinding.ItemExpenseBinding
import com.example.expensetrackerapp.model.Expense
import com.example.expensetrackerapp.utils.CurrencyUtils
import com.example.expensetrackerapp.utils.DateUtils

/**
 * RecyclerView adapter for displaying Expense items.
 * Accepts a currency symbol so amounts are always shown in the user's chosen currency.
 */
class ExpenseAdapter(
    private val onItemClick: (Expense) -> Unit
) : ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder>(DiffCallback) {

    /** Current currency symbol — update via updateCurrency() to refresh all items. */
    var currencySymbol: String = "₹"
        private set

    /** Call this when the user changes their currency preference. */
    fun updateCurrency(symbol: String) {
        currencySymbol = symbol
        notifyDataSetChanged()
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Expense, newItem: Expense) = oldItem == newItem
    }

    inner class ExpenseViewHolder(private val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: Expense) {
            // Format amount using current currency with exchange rate conversion
            binding.tvAmount.text = CurrencyUtils.format(expense.amount, currencySymbol)
            binding.tvDate.text = DateUtils.formatDisplayDate(expense.date)
            binding.tvNote.text = expense.note.ifBlank { "No note" }

            binding.tvCategoryEmoji.text = getCategoryEmoji(expense.category)
            binding.chipCategory.text = expense.category
            binding.chipCategory.setChipBackgroundColorResource(getCategoryColor(expense.category))

            binding.root.setOnClickListener { onItemClick(expense) }
        }

        private fun getCategoryEmoji(category: String) = when (category) {
            "Food"       -> "🍔"
            "Travel"     -> "✈️"
            "Shopping"   -> "🛍️"
            "Healthcare" -> "💊"
            else         -> "📦"
        }

        private fun getCategoryColor(category: String) = when (category) {
            "Food"       -> R.color.cat_food
            "Travel"     -> R.color.cat_travel
            "Shopping"   -> R.color.cat_shopping
            "Healthcare" -> R.color.cat_health
            else         -> R.color.cat_other
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
