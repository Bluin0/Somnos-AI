package com.example.api

import com.example.BuildConfig

actual fun getGeminiApiKey(): String {
    return BuildConfig.GEMINI_API_KEY
}
