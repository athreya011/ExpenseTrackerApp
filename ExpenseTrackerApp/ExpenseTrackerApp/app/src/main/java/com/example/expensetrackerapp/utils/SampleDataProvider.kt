package com.example.expensetrackerapp.utils

import com.example.expensetrackerapp.model.Category
import com.example.expensetrackerapp.model.User

/**
 * Provides seed data for new users.
 * No sample expenses — users start with a clean slate.
 */
object SampleDataProvider {

    /** The built-in demo user. */
    fun getDemoUser(): User = User(
        username = "demo",
        email = "demo@test.com",
        password = "demo123"
    )

    /** Default categories seeded for every new user on first login/signup. */
    fun getDefaultCategories(): List<Category> = listOf(
        Category(id = "cat_food",     name = "Food",       isDefault = true),
        Category(id = "cat_travel",   name = "Travel",     isDefault = true),
        Category(id = "cat_shopping", name = "Shopping",   isDefault = true),
        Category(id = "cat_others",   name = "Others",     isDefault = true),
        Category(id = "cat_health",   name = "Healthcare", isDefault = false)
    )
}
