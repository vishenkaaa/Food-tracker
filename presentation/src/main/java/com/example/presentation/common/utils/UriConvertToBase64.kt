package com.example.presentation.common.utils

import android.content.Context
import android.util.Base64
import androidx.core.net.toUri

fun uriToBase64(context: Context, uri: String): String {
    val inputStream = context.contentResolver.openInputStream(uri.toUri())
    val bytes = inputStream?.readBytes() ?: return ""
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}