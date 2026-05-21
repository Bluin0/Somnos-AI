package com.example.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberPlatformMediaPicker(onMediaPicked: (ByteArray, String) -> Unit): PlatformMediaPicker {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                if (bytes != null) {
                    val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
                    onMediaPicked(bytes, mimeType)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    return remember {
        object : PlatformMediaPicker {
            override fun launch(mimeType: String) {
                launcher.launch(mimeType)
            }
        }
    }
}
