package com.example.expensetrackerapp.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Utility functions for date formatting and comparison. */
object DateUtils {

    private const val DATE_FORMAT = "yyyy-MM-dd"
    private const val DISPLAY_FORMAT = "dd MMM yyyy"
    private const val MONTH_FORMAT = "yyyy-MM"

    /** Formats a timestamp (millis) to display date string (dd MMM yyyy). */
    fun formatDate(timestamp: Long): String {
        return SimpleDateFormat(DISPLAY_FORMAT, Locale.getDefault()).format(Date(timestamp))
    }

    /** Returns today's date as yyyy-MM-dd string. */
    fun getCurrentDate(): String {
        return SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
    }

    /** Extracts yyyy-MM month string from a yyyy-MM-dd date string. */
    fun getMonthYear(date: String): String {
        return if (date.length >= 7) date.substring(0, 7) else ""
    }

    /** Returns current month as yyyy-MM string. */
    fun getCurrentMonthYear(): String {
        return SimpleDateFormat(MONTH_FORMAT, Locale.getDefault()).format(Date())
    }

    /** Returns a human-readable month label like "January 2024". */
    fun getMonthLabel(monthYear: String): String {
        return try {
            val date = SimpleDateFormat(MONTH_FORMAT, Locale.getDefault()).parse(monthYear)!!
            SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(date)
        } catch (e: Exception) {
            monthYear
        }
    }

    /** Adds months to a yyyy-MM string and returns the new yyyy-MM string. */
    fun addMonths(monthYear: String, delta: Int): String {
        return try {
            val sdf = SimpleDateFormat(MONTH_FORMAT, Locale.getDefault())
            val cal = Calendar.getInstance()
            cal.time = sdf.parse(monthYear)!!
            cal.add(Calendar.MONTH, delta)
            sdf.format(cal.time)
        } catch (e: Exception) {
            monthYear
        }
    }

    /** Compares two yyyy-MM-dd date strings. Returns negative if d1 < d2. */
    fun compareDates(d1: String, d2: String): Int = d1.compareTo(d2)

    /** Formats a yyyy-MM-dd date string to display format (dd MMM yyyy). */
    fun formatDisplayDate(dateStr: String): String {
        return try {
            val date = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).parse(dateStr)!!
            SimpleDateFormat(DISPLAY_FORMAT, Locale.getDefault()).format(date)
        } catch (e: Exception) {
            dateStr
        }
    }
}
