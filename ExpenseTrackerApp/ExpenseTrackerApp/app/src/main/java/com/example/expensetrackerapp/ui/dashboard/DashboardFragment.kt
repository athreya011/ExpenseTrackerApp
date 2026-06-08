package com.example.expensetrackerapp.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetrackerapp.R
import com.example.expensetrackerapp.databinding.FragmentDashboardBinding
import com.example.expensetrackerapp.ui.expense.ExpenseAdapter
import com.example.expensetrackerapp.utils.CurrencyUtils

/**
 * Dashboard screen showing greeting, total expenses, category summary, and recent transactions.
 * All amounts are converted using CurrencyUtils based on the user's saved currency preference.
 */
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var expenseAdapter: ExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarMenu()
        setupRecyclerView()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        // Reload on every resume so currency changes from Profile are reflected immediately
        viewModel.loadDashboardData()
    }

    private fun setupToolbarMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.dashboard_menu, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_manage_categories -> {
                        findNavController().navigate(R.id.categoryManagerFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter { expense ->
            val bundle = Bundle().apply { putString("expenseId", expense.id) }
            findNavController().navigate(R.id.addEditExpenseFragment, bundle)
        }
        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentTransactions.adapter = expenseAdapter
    }

    private fun setupObservers() {
        viewModel.greeting.observe(viewLifecycleOwner) {
            binding.tvGreeting.text = it
        }

        // When currency changes, update adapter AND all amount labels
        viewModel.currencySymbol.observe(viewLifecycleOwner) { currency ->
            expenseAdapter.updateCurrency(currency)

            // Re-apply totals with new currency
            viewModel.totalExpenses.value?.let { total ->
                binding.tvTotalAmount.text = CurrencyUtils.format(total, currency)
            }
            viewModel.categoryWiseSummary.value?.let { summary ->
                binding.tvFoodAmount.text     = CurrencyUtils.format(summary["Food"]     ?: 0.0, currency)
                binding.tvTravelAmount.text   = CurrencyUtils.format(summary["Travel"]   ?: 0.0, currency)
                binding.tvShoppingAmount.text = CurrencyUtils.format(summary["Shopping"] ?: 0.0, currency)
                binding.tvOthersAmount.text   = CurrencyUtils.format(summary["Others"]   ?: 0.0, currency)
            }
        }

        viewModel.totalExpenses.observe(viewLifecycleOwner) { total ->
            val currency = viewModel.currencySymbol.value ?: "₹"
            binding.tvTotalAmount.text = CurrencyUtils.format(total, currency)
        }

        viewModel.categoryWiseSummary.observe(viewLifecycleOwner) { summary ->
            val currency = viewModel.currencySymbol.value ?: "₹"
            binding.tvFoodAmount.text     = CurrencyUtils.format(summary["Food"]     ?: 0.0, currency)
            binding.tvTravelAmount.text   = CurrencyUtils.format(summary["Travel"]   ?: 0.0, currency)
            binding.tvShoppingAmount.text = CurrencyUtils.format(summary["Shopping"] ?: 0.0, currency)
            binding.tvOthersAmount.text   = CurrencyUtils.format(summary["Others"]   ?: 0.0, currency)
        }

        viewModel.recentTransactions.observe(viewLifecycleOwner) { expenses ->
            expenseAdapter.submitList(expenses)
            binding.tvNoTransactions.visibility =
                if (expenses.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
