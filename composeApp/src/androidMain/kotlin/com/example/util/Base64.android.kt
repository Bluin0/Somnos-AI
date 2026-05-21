package com.example.util

import android.util.Base64

actual fun encodeBase64(bytes: ByteArray): String {
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}
