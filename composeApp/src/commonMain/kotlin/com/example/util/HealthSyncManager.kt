package com.example.util

import androidx.compose.runtime.Composable
import com.example.api.SleepExtractedData

interface HealthSyncManager {
    val isSupported: Boolean
    fun requestPermission(onResult: (Boolean) -> Unit)
    fun fetchSleepData(onResult: (SleepExtractedData?) -> Unit)
}

@Composable
expect fun rememberHealthSyncManager(): HealthSyncManager
