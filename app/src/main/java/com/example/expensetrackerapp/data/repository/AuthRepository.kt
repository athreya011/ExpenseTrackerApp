package com.example.expensetrackerapp.data.repository

import com.example.expensetrackerapp.data.local.SharedPreferencesManager
import com.example.expensetrackerapp.model.User
import com.example.expensetrackerapp.utils.SampleDataProvider

/**
 * Repository for authentication operations.
 * On first login for any user, seeds default categories only — no sample expenses.
 * Each user's data is completely isolated.
 */
class AuthRepository(private val prefs: SharedPreferencesManager) {

    init {
        // Seed the global users list with demo user if it's empty
        if (prefs.getUsers().isEmpty()) {
            prefs.saveUsers(listOf(SampleDataProvider.getDemoUser()))
        }
    }

    fun isLoggedIn(): Boolean = prefs.isLoggedIn()

    fun getLoggedInUser(): User? = prefs.getLoggedInUser()

    /**
     * Attempts login. On first login for a user, seeds their default categories.
     * Returns the User on success, null on failure.
     */
    fun login(usernameOrEmail: String, password: String): User? {
        val users = prefs.getUsers()
        val user = users.find {
            (it.username == usernameOrEmail || it.email == usernameOrEmail) && it.password == password
        } ?: return null

        prefs.saveLoggedInUser(user)

        // Seed default categories for this user on their very first login
        if (prefs.isFirstRunForUser(user.username)) {
            prefs.saveCategories(SampleDataProvider.getDefaultCategories())
            prefs.setFirstRunDoneForUser(user.username)
        }

        return user
    }

    /**
     * Registers a new user and seeds their default categories immediately.
     * Returns null on success, or an error message on failure.
     */
    fun signup(username: String, email: String, password: String): String? {
        val users = prefs.getUsers().toMutableList()
        if (users.any { it.username == username }) return "Username already taken"
        if (users.any { it.email == email }) return "Email already registered"

        val newUser = User(username = username, email = email, password = password)
        users.add(newUser)
        prefs.saveUsers(users)
        prefs.saveLoggedInUser(newUser)

        // Seed default categories for the new user
        prefs.saveCategories(SampleDataProvider.getDefaultCategories())
        prefs.setFirstRunDoneForUser(newUser.username)

        return null
    }

    fun logout() = prefs.clearSession()
}
