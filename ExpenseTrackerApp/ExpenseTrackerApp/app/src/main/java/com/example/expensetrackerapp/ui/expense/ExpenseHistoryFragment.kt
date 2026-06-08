package com.example.expensetrackerapp.ui.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetrackerapp.R
import com.example.expensetrackerapp.databinding.FragmentExpenseHistoryBinding

/**
 * Fragment showing all expenses with category filter and sort options.
 * Tapping an item navigates to AddExpenseFragment in edit mode.
 */
class ExpenseHistoryFragment : Fragment() {

    private var _binding: FragmentExpenseHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExpenseHistoryViewModel by viewModels()
    private lateinit var expenseAdapter: ExpenseAdapter
    private var spinnerReady = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSortMenu()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        spinnerReady = false
        // Refresh currency on every resume so changes from Profile are reflected
        val currency = com.example.expensetrackerapp.data.local.SharedPreferencesManager
            .getInstance(requireContext()).getCurrencySymbol()
        expenseAdapter.updateCurrency(currency)
        viewModel.loadData()
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter { expense ->
            val bundle = Bundle().apply { putString("expenseId", expense.id) }
            findNavController().navigate(R.id.addEditExpenseFragment, bundle)
        }
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = expenseAdapter
    }

    private fun setupSortMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.history_sort_menu, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.sort_date_desc -> { viewModel.setSortOrder(SortOrder.DATE_DESC); true }
                    R.id.sort_date_asc -> { viewModel.setSortOrder(SortOrder.DATE_ASC); true }
                    R.id.sort_amount_desc -> { viewModel.setSortOrder(SortOrder.AMOUNT_DESC); true }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupObservers() {
        viewModel.categoryNames.observe(viewLifecycleOwner) { names ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerFilter.adapter = adapter

            binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                    if (!spinnerReady) { spinnerReady = true; return }
                    viewModel.setFilter(names[pos])
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        viewModel.filteredExpenses.observe(viewLifecycleOwner) { expenses ->
            expenseAdapter.submitList(expenses)
            binding.tvEmptyState.visibility = if (expenses.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
