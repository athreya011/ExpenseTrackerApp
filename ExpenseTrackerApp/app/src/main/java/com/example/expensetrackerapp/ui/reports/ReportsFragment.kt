package com.example.expensetrackerapp.ui.reports

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.expensetrackerapp.databinding.FragmentReportsBinding
import com.example.expensetrackerapp.utils.DateUtils
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

/**
 * Fragment showing monthly expense reports with PieChart and BarChart.
 * Supports previous/next month navigation.
 */
class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReportsViewModel by viewModels()

    private val chartColors = listOf(
        Color.parseColor("#FF9800"), Color.parseColor("#2196F3"),
        Color.parseColor("#E91E63"), Color.parseColor("#9E9E9E"),
        Color.parseColor("#4CAF50"), Color.parseColor("#9C27B0"),
        Color.parseColor("#00BCD4"), Color.parseColor("#FF5722")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCharts()
        setupObservers()
        setupMonthNavigation()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadReportData()
    }

    private fun setupMonthNavigation() {
        binding.btnPrevMonth.setOnClickListener { viewModel.previousMonth() }
        binding.btnNextMonth.setOnClickListener { viewModel.nextMonth() }
    }

    private fun setupCharts() {
        with(binding.pieChart) {
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            holeRadius = 40f
            legend.isEnabled = true
            setEntryLabelColor(Color.BLACK)
        }
        with(binding.barChart) {
            description.isEnabled = false
            legend.isEnabled = false
            xAxis.granularity = 1f
            axisRight.isEnabled = false
        }
    }

    private fun setupObservers() {
        viewModel.selectedMonth.observe(viewLifecycleOwner) { month ->
            binding.tvMonthLabel.text = DateUtils.getMonthLabel(month)
        }

        viewModel.categoryBreakdown.observe(viewLifecycleOwner) { breakdown ->
            if (breakdown.isEmpty()) {
                binding.pieChart.clear()
                binding.pieChart.centerText = "No data"
                return@observe
            }
            val total = breakdown.values.sum()
            val entries = breakdown.entries.map { (cat, amount) ->
                val pct = if (total > 0) (amount / total * 100) else 0.0
                PieEntry(pct.toFloat(), cat)
            }
            val dataSet = PieDataSet(entries, "").apply {
                colors = chartColors.take(entries.size)
                valueTextSize = 12f
                valueTextColor = Color.WHITE
                // Format labels as "XX.X%"
                valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                    override fun getFormattedValue(value: Float) = "%.1f%%".format(value)
                }
            }
            binding.pieChart.apply {
                data = PieData(dataSet)
                setUsePercentValues(false) // we already calculated percentages
                invalidate()
            }
        }

        viewModel.dailyBreakdown.observe(viewLifecycleOwner) { daily ->
            if (daily.isEmpty()) {
                binding.barChart.clear()
                return@observe
            }
            val labels = daily.keys.map { it.takeLast(2) } // day number
            val entries = daily.values.mapIndexed { i, v -> BarEntry(i.toFloat(), v.toFloat()) }
            val dataSet = BarDataSet(entries, "Daily Expenses").apply {
                color = Color.parseColor("#1976D2")
                valueTextSize = 10f
            }
            binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            binding.barChart.data = BarData(dataSet)
            binding.barChart.invalidate()
        }

        viewModel.monthlyExpenses.observe(viewLifecycleOwner) { expenses ->
            val currency = com.example.expensetrackerapp.data.local.SharedPreferencesManager
                .getInstance(requireContext()).getCurrencySymbol()
            val total = expenses.sumOf { it.amount }
            binding.tvTotalMonth.text = com.example.expensetrackerapp.utils.CurrencyUtils.format(total, currency)
            binding.tvTransactionCount.text = "${expenses.size} transactions"
            val topCategory = expenses.groupBy { it.category }
                .maxByOrNull { (_, list) -> list.sumOf { it.amount } }?.key ?: "—"
            binding.tvHighestCategory.text = topCategory
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
