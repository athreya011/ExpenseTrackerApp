package com.example.expensetrackerapp.data.repository

import com.example.expensetrackerapp.data.local.SharedPreferencesManager
import com.example.expensetrackerapp.model.Category

/**
 * Repository for CRUD operations on Category data stored in SharedPreferences.
 */
class CategoryRepository(private val prefs: SharedPreferencesManager) {

    /** Returns all categories. */
    fun getCategories(): List<Category> = prefs.getCategories()

    /** Returns category names as a list of strings. */
    fun getCategoryNames(): List<String> = getCategories().map { it.name }

    /** Adds a new custom category. */
    fun addCategory(category: Category) {
        val list = prefs.getCategories().toMutableList()
        list.add(category)
        prefs.saveCategories(list)
    }

    /** Deletes a non-default category by id. */
    fun deleteCategory(categoryId: String) {
        val list = prefs.getCategories().toMutableList()
        list.removeAll { it.id == categoryId && !it.isDefault }
        prefs.saveCategories(list)
    }

    /** Returns a category by id, or null. */
    fun getCategory(id: String): Category? = getCategories().find { it.id == id }

    /** Returns a category by name, or null. */
    fun getCategoryByName(name: String): Category? = getCategories().find { it.name == name }
}
