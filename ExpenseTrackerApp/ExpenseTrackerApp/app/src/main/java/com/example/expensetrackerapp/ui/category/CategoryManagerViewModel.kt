package com.example.expensetrackerapp.ui.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.expensetrackerapp.data.local.SharedPreferencesManager
import com.example.expensetrackerapp.data.repository.CategoryRepository
import com.example.expensetrackerapp.model.Category
import java.util.UUID

/**
 * ViewModel for CategoryManagerFragment.
 * Handles loading, adding, and deleting categories.
 */
class CategoryManagerViewModel(application: Application) : AndroidViewModel(application) {

    private val categoryRepository = CategoryRepository(SharedPreferencesManager.getInstance(application))

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    /** Loads all categories from repository. */
    fun loadCategories() {
        _categories.value = categoryRepository.getCategories()
    }

    /**
     * Adds a new custom category.
     * Validates that the name is not empty and not a duplicate.
     */
    fun addCategory(name: String) {
        val trimmed = name.trim()
        if (trimmed.isBlank()) {
            _error.value = "Category name cannot be empty"
            return
        }
        val existing = categoryRepository.getCategories()
        if (existing.any { it.name.equals(trimmed, ignoreCase = true) }) {
            _error.value = "Category '$trimmed' already exists"
            return
        }
        val category = Category(id = UUID.randomUUID().toString(), name = trimmed, isDefault = false)
        categoryRepository.addCategory(category)
        loadCategories()
    }

    /** Deletes a non-default category by id. */
    fun deleteCategory(categoryId: String) {
        categoryRepository.deleteCategory(categoryId)
        loadCategories()
    }
}
