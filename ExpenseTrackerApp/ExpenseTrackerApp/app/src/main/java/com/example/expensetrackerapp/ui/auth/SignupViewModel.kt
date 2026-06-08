package com.example.expensetrackerapp.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.expensetrackerapp.data.local.SharedPreferencesManager
import com.example.expensetrackerapp.data.repository.AuthRepository
import com.example.expensetrackerapp.utils.ValidationUtils

/**
 * ViewModel for SignupActivity.
 * Handles field-level validation and user registration via AuthRepository.
 */
class SignupViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(SharedPreferencesManager.getInstance(application))

    private val _signupSuccess = MutableLiveData<Boolean>()
    val signupSuccess: LiveData<Boolean> = _signupSuccess

    // Field-level error LiveData
    private val _usernameError = MutableLiveData<String?>()
    val usernameError: LiveData<String?> = _usernameError

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    private val _confirmPasswordError = MutableLiveData<String?>()
    val confirmPasswordError: LiveData<String?> = _confirmPasswordError

    private val _generalError = MutableLiveData<String>()
    val generalError: LiveData<String> = _generalError

    /**
     * Validates all fields and attempts signup.
     * Posts field-level errors or signupSuccess.
     */
    fun signup(username: String, email: String, password: String, confirmPassword: String) {
        var valid = true

        if (!ValidationUtils.isNotEmpty(username)) {
            _usernameError.value = "Username is required"; valid = false
        } else {
            _usernameError.value = null
        }

        if (!ValidationUtils.isValidEmail(email)) {
            _emailError.value = "Enter a valid email address"; valid = false
        } else {
            _emailError.value = null
        }

        if (!ValidationUtils.isValidPassword(password)) {
            _passwordError.value = "Password must be at least 6 characters"; valid = false
        } else {
            _passwordError.value = null
        }

        if (password != confirmPassword) {
            _confirmPasswordError.value = "Passwords do not match"; valid = false
        } else {
            _confirmPasswordError.value = null
        }

        if (!valid) return

        val error = authRepository.signup(username.trim(), email.trim(), password.trim())
        if (error != null) {
            _generalError.value = error
        } else {
            _signupSuccess.value = true
        }
    }
}
