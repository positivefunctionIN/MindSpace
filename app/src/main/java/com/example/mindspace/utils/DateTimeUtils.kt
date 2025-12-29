package com.example.mindspace.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    fun formatReminderTime(timestamp: Long): String {
        val now = Calendar.getInstance()
        val reminder = Calendar.getInstance().apply { timeInMillis = timestamp }

        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        if (timestamp < System.currentTimeMillis()) {
            return "Overdue"
        }

        val isToday = now.get(Calendar.YEAR) == reminder.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == reminder.get(Calendar.DAY_OF_YEAR)

        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
        val isTomorrow = tomorrow.get(Calendar.YEAR) == reminder.get(Calendar.YEAR) &&
                tomorrow.get(Calendar.DAY_OF_YEAR) == reminder.get(Calendar.DAY_OF_YEAR)

        return when {
            isToday -> "Today at ${timeFormat.format(Date(timestamp))}"
            isTomorrow -> "Tomorrow at ${timeFormat.format(Date(timestamp))}"
            else -> SimpleDateFormat("MMM dd 'at' hh:mm a", Locale.getDefault())
                .format(Date(timestamp))
        }
    }

    fun formatRelativeTime(timestamp: Long): String {
        val diff = timestamp - System.currentTimeMillis()
        if (diff < 0) return "Overdue"

        val minutes = diff / (60 * 1000)
        val hours = diff / (60 * 60 * 1000)
        val days = diff / (24 * 60 * 60 * 1000)

        return when {
            minutes < 1 -> "Less than a minute"
            minutes < 60 -> "In ${minutes}m"
            hours < 24 -> "In ${hours}h"
            days < 7 -> "In ${days}d"
            else -> formatReminderTime(timestamp)
        }
    }

    fun getTomorrowMorning(hour: Int = 9, minute: Int = 0): Long {
        return Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun getInOneHour(): Long {
        return System.currentTimeMillis() + (60 * 60 * 1000)
    }

    fun getNextWeek(): Long {
        return Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 7)
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}