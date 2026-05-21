package com.example.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberPlatformMediaPicker(onMediaPicked: (ByteArray, String) -> Unit): PlatformMediaPicker {
    return remember {
        object : PlatformMediaPicker {
            override fun launch(mimeType: String) {
                // Stub implementation for compilation.
                // When compiled on macOS/iOS, can be integrated with UIDocumentPickerViewController
                // or PHPickerViewController if desired.
            }
        }
    }
}
