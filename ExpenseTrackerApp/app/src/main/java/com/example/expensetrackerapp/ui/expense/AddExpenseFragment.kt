package com.example.expensetrackerapp.ui.expense

import android.util.Log
import com.example.expensetrackerapp.api.CurrencyResponse
import com.example.expensetrackerapp.api.RetrofitClient
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.expensetrackerapp.data.local.SharedPreferencesManager
import com.example.expensetrackerapp.databinding.FragmentAddExpenseBinding
import com.example.expensetrackerapp.model.Expense
import com.example.expensetrackerapp.utils.CurrencyUtils
import com.example.expensetrackerapp.utils.DateUtils
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar
import java.util.UUID

/**
 * Fragment for adding or editing an expense.
 * The amount field always shows values in the user's selected currency.
 * On save, the amount is converted back to INR for consistent storage.
 */
class AddExpenseFragment : Fragment() {

    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddExpenseViewModel by viewModels()

    private var expenseId: String? = null
    private var categoryNames: List<String> = emptyList()
    private var currencySymbol: String = "₹"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        expenseId = arguments?.getString("expenseId")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val apiKey = "52ad8f4e0b486df59e127d07"

        RetrofitClient.api.getRates(apiKey, "USD")
            .enqueue(object : retrofit2.Callback<CurrencyResponse> {

                override fun onResponse(
                    call: retrofit2.Call<CurrencyResponse>,
                    response: retrofit2.Response<CurrencyResponse>
                ) {

                    if (response.isSuccessful) {

                        val rates = response.body()?.conversion_rates

                        val inrRate = rates?.get("INR")

                        Log.d("API_TEST", "USD to INR = $inrRate")
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<CurrencyResponse>,
                    t: Throwable
                ) {

                    Log.d("API_TEST", "ERROR = ${t.message}")
                }
            })
        // Load the user's current currency
        currencySymbol = SharedPreferencesManager.getInstance(requireContext()).getCurrencySymbol()

        // Set currency symbol as the amount field prefix
        binding.tilAmount.prefixText = "$currencySymbol  "

        viewModel.loadCategories()
        setupObservers()
        setupDatePicker()
        setupButtons()

        if (binding.etDate.text.isNullOrBlank()) {
            binding.etDate.setText(DateUtils.getCurrentDate())
        }

        if (expenseId != null) {
            binding.toolbar.text = "Edit Expense"
            binding.btnDelete.visibility = View.VISIBLE
        } else {
            binding.toolbar.text = "Add Expense"
        }
    }

    private fun setupObservers() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryNames = categories.map { it.name }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categoryNames
            )
            binding.actvCategory.setAdapter(adapter)

            // Prefill fields in edit mode — show amount in user's currency
            expenseId?.let { id ->
                val expense = viewModel.getExpense(id)
                if (expense != null) {
                    // Convert stored INR amount to user's currency for display
                    val displayAmount = CurrencyUtils.convert(expense.amount, currencySymbol)
                    binding.etAmount.setText("%.2f".format(displayAmount))
                    binding.actvCategory.setText(expense.category, false)
                    binding.etDate.setText(expense.date)
                    binding.etNote.setText(expense.note)
                }
            }
        }

        viewModel.validationError.observe(viewLifecycleOwner) { msg ->
            Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
        }

        viewModel.operationSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Snackbar.make(binding.root, "Saved successfully", Snackbar.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    binding.etDate.setText("%04d-%02d-%02d".format(year, month + 1, day))
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            val amountStr = binding.etAmount.text.toString().trim()
            val category = binding.actvCategory.text.toString().trim()
            val date = binding.etDate.text.toString().trim()
            val note = binding.etNote.text.toString().trim()

            val enteredAmount = amountStr.toDoubleOrNull() ?: 0.0

            // Convert entered amount back to INR for storage
            // e.g. if user entered $5, store as ₹416 (5 / 0.012)
            val amountInINR = convertToINR(enteredAmount, currencySymbol)

            val expense = Expense(
                id = expenseId ?: UUID.randomUUID().toString(),
                amount = amountInINR,
                category = category,
                date = date,
                note = note,
                timestamp = System.currentTimeMillis()
            )

            if (expenseId != null) {
                viewModel.updateExpense(expense)
            } else {
                viewModel.saveExpense(expense)
            }
        }

        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to delete this expense?")
                .setPositiveButton("Delete") { _, _ ->
                    expenseId?.let { viewModel.deleteExpense(it) }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    /**
     * Converts an amount from the given currency back to INR for storage.
     * Since CurrencyUtils.convert() goes INR → currency, we divide to reverse it.
     */
    private fun convertToINR(amount: Double, fromCurrency: String): Double {
        if (fromCurrency == "₹") return amount
        val rateFromINR = when (fromCurrency) {
            "$"  -> 0.012
            "€"  -> 0.011
            "£"  -> 0.0095
            else -> 1.0
        }
        return amount / rateFromINR
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
