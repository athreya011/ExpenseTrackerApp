package com.example.expensetrackerapp.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.expensetrackerapp.data.local.SharedPreferencesManager
import com.example.expensetrackerapp.data.repository.AuthRepository
import com.example.expensetrackerapp.model.User

/**
 * ViewModel for ProfileFragment.
 * Exposes current user info, currency setting, and logout action.
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = SharedPreferencesManager.getInstance(application)
    private val authRepository = AuthRepository(prefs)

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _currencySymbol = MutableLiveData<String>()
    val currencySymbol: LiveData<String> = _currencySymbol

    private val _loggedOut = MutableLiveData<Boolean>()
    val loggedOut: LiveData<Boolean> = _loggedOut

    /** Loads the currently logged-in user. */
    fun loadUser() {
        _currentUser.value = authRepository.getLoggedInUser()
        _currencySymbol.value = prefs.getCurrencySymbol()
    }

    /** Saves the selected currency symbol. */
    fun saveCurrencySymbol(symbol: String) {
        prefs.saveCurrencySymbol(symbol)
        _currencySymbol.value = symbol
    }

    /** Logs out the current user and signals navigation. */
    fun logout() {
        authRepository.logout()
        _loggedOut.value = true
    }
}
