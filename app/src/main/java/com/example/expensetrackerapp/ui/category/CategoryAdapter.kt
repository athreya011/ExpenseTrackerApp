package com.example.expensetrackerapp.ui.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.databinding.ItemCategoryBinding
import com.example.expensetrackerapp.model.Category

/**
 * RecyclerView adapter for displaying Category items.
 * Default categories show no delete button; custom categories show a delete icon.
 */
class CategoryAdapter(
    private val onDeleteClick: (Category) -> Unit
) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Category, newItem: Category) = oldItem == newItem
    }

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /** Binds a Category to the view. */
        fun bind(category: Category) {
            binding.tvCategoryName.text = category.name
            binding.tvCategoryEmoji.text = when (category.name) {
                "Food" -> "🍔"
                "Travel" -> "✈️"
                "Shopping" -> "🛍️"
                "Healthcare" -> "💊"
                else -> "📦"
            }
            if (category.isDefault) {
                binding.ivDelete.visibility = View.INVISIBLE
                binding.tvDefaultBadge.visibility = View.VISIBLE
            } else {
                binding.ivDelete.visibility = View.VISIBLE
                binding.tvDefaultBadge.visibility = View.GONE
                binding.ivDelete.setOnClickListener { onDeleteClick(category) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
