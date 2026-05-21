package com.example.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun getCurrentTimestamp(): Long {
    return System.currentTimeMillis()
}

actual fun getCurrentFormattedDate(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}

