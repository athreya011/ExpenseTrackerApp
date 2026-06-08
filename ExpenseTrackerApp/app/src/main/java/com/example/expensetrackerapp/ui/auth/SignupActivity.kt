package com.example.expensetrackerapp.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.expensetrackerapp.databinding.ActivitySignupBinding
import com.example.expensetrackerapp.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar

/**
 * Activity for new user registration.
 * Shows field-level validation errors via TextInputLayout.
 */
class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val viewModel: SignupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.signupSuccess.observe(this) { if (it) goToMain() }
        viewModel.usernameError.observe(this) { binding.tilUsername.error = it }
        viewModel.emailError.observe(this) { binding.tilEmail.error = it }
        viewModel.passwordError.observe(this) { binding.tilPassword.error = it }
        viewModel.confirmPasswordError.observe(this) { binding.tilConfirmPassword.error = it }
        viewModel.generalError.observe(this) { msg ->
            Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun setupClickListeners() {
        binding.btnSignup.setOnClickListener {
            viewModel.signup(
                username = binding.etUsername.text.toString(),
                email = binding.etEmail.text.toString(),
                password = binding.etPassword.text.toString(),
                confirmPassword = binding.etConfirmPassword.text.toString()
            )
        }

        binding.tvGoToLogin.setOnClickListener { finish() }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }
}
