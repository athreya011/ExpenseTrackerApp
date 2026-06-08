package com.example.expensetrackerapp.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.expensetrackerapp.data.local.SharedPreferencesManager
import com.example.expensetrackerapp.data.repository.AuthRepository
import com.example.expensetrackerapp.utils.ValidationUtils

/**
 * ViewModel for LoginActivity.
 * Handles credential validation and login via AuthRepository.
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(SharedPreferencesManager.getInstance(application))

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    /** Returns true if a session is already active (auto-login). */
    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()

    /**
     * Validates inputs and attempts login.
     * Posts to loginSuccess or errorMessage accordingly.
     */
    fun login(usernameOrEmail: String, password: String) {
        when {
            !ValidationUtils.isNotEmpty(usernameOrEmail) ->
                _errorMessage.value = "Username or email is required"
            !ValidationUtils.isNotEmpty(password) ->
                _errorMessage.value = "Password is required"
            !ValidationUtils.isValidPassword(password) ->
                _errorMessage.value = "Password must be at least 6 characters"
            else -> {
                val user = authRepository.login(usernameOrEmail.trim(), password.trim())
                if (user != null) {
                    _loginSuccess.value = true
                } else {
                    _errorMessage.value = "Invalid username/email or password"
                }
            }
        }
    }
}
