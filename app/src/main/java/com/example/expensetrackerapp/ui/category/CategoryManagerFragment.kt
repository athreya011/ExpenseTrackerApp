package com.example.expensetrackerapp.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetrackerapp.databinding.FragmentCategoryManagerBinding
import com.google.android.material.snackbar.Snackbar

/**
 * Fragment for managing expense categories.
 * Default categories are shown but cannot be deleted.
 * Custom categories can be added and deleted.
 */
class CategoryManagerFragment : Fragment() {

    private var _binding: FragmentCategoryManagerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryManagerViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupAddButton()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCategories()
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter { category ->
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Category")
                .setMessage("Delete '${category.name}'? Expenses in this category will remain.")
                .setPositiveButton("Delete") { _, _ -> viewModel.deleteCategory(category.id) }
                .setNegativeButton("Cancel", null)
                .show()
        }
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategories.adapter = categoryAdapter
    }

    private fun setupObservers() {
        viewModel.categories.observe(viewLifecycleOwner) { list ->
            categoryAdapter.submitList(list)
        }
        viewModel.error.observe(viewLifecycleOwner) { msg ->
            Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun setupAddButton() {
        binding.btnAddCategory.setOnClickListener {
            val name = binding.etCategoryName.text.toString()
            viewModel.addCategory(name)
            binding.etCategoryName.text?.clear()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
