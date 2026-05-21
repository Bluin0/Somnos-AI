package com.example.api

import platform.Foundation.NSProcessInfo

actual fun getGeminiApiKey(): String {
    val envKey = NSProcessInfo.processInfo.environment["GEMINI_API_KEY"] as? String
    if (!envKey.isNullOrBlank()) {
        return envKey
    }
    return ""
}
