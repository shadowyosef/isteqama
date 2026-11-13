package com.example.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CompletedLockRecord(
    val id: String,
    val title: String,
    val lockType: String,
    val targetAmount: Int,
    val completedAt: Long = System.currentTimeMillis()
) {
    fun formattedTime(): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(completedAt))
    }
}
