package com.example.baseproject.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    fun convertTimestampToDateTime(timestamp: Long) : String {
        val currentTimeMillis = System.currentTimeMillis()
        val date = Date(timestamp)
        val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())
        val sdfDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        return when {
            sdfDate.format(date) == sdfDate.format(Date(currentTimeMillis)) -> sdfTime.format(date)
            sdfDate.format(date) == sdfDate.format(Date(currentTimeMillis - 24 * 60 * 60 * 1000)) -> Constants.IS_YESTERDAY
            else -> sdfDate.format(date)
        }
    }
}