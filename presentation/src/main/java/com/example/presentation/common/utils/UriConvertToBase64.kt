package com.example.presentation.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.core.net.toUri
import java.io.ByteArrayOutputStream
import androidx.core.graphics.scale

//fun uriToBase64(context: Context, uri: String): String {
//    val inputStream = context.contentResolver.openInputStream(uri.toUri())
//    val bytes = inputStream?.readBytes() ?: return ""
//    return Base64.encodeToString(bytes, Base64.NO_WRAP)
//}

fun uriToBase64(context: Context, uri: String): String {
    val inputStream = context.contentResolver.openInputStream(uri.toUri()) ?: return ""
    val originalBytes = inputStream.readBytes()

    val bitmap = BitmapFactory.decodeByteArray(originalBytes, 0, originalBytes.size)

    val scaledBitmap = bitmap.scale(
        800,
        (bitmap.height * 800f / bitmap.width).toInt()
    )

    val stream = ByteArrayOutputStream()
    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
    val compressedBytes = stream.toByteArray()

    val base64 = Base64.encodeToString(compressedBytes, Base64.NO_WRAP)

    return base64
}