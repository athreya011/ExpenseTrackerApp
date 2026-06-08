package com.example.expensetrackerapp.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.expensetrackerapp.databinding.ActivityLoginBinding
import com.example.expensetrackerapp.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar

/**
 * Launcher activity that handles login.
 * Checks for an existing session and redirects to MainActivity if already logged in.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Auto-navigate if session exists
        if (viewModel.isLoggedIn()) {
            goToMain()
            return
        }

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.loginSuccess.observe(this) { success ->
            if (success) goToMain()
        }
        viewModel.errorMessage.observe(this) { msg ->
            Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val usernameOrEmail = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(usernameOrEmail, password)
        }

        binding.tvGoToSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
