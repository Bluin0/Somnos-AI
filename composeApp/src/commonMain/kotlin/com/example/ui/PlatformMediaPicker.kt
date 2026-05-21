package com.example.ui

import androidx.compose.runtime.Composable

@Composable
expect fun rememberPlatformMediaPicker(onMediaPicked: (ByteArray, String) -> Unit): PlatformMediaPicker

interface PlatformMediaPicker {
    fun launch(mimeType: String)
}
