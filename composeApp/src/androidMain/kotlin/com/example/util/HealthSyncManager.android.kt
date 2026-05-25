package com.example.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.api.SleepExtractedData

class AndroidHealthSyncManager : HealthSyncManager {
    override val isSupported: Boolean = false
    override fun requestPermission(onResult: (Boolean) -> Unit) {
        onResult(false)
    }
    override fun fetchSleepData(onResult: (SleepExtractedData?) -> Unit) {
        onResult(null)
    }
}

@Composable
actual fun rememberHealthSyncManager(): HealthSyncManager {
    return remember { AndroidHealthSyncManager() }
}
