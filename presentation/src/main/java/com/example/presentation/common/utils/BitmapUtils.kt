package com.example.presentation.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.example.presentation.R

fun createProgressCircleBitmap(
    progress: Float,
    sizePx: Int,
    strokeWidthPx: Float,
    progressColor: Int = R.color.green,
    trackColor: Int = R.color.green_30,
    context: Context
): Bitmap {
    val bitmap = createBitmap(sizePx, sizePx)
    val canvas = Canvas(bitmap)

    val centerX = sizePx / 2f
    val centerY = sizePx / 2f
    val radius = (sizePx - strokeWidthPx) / 2f

    val trackPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = strokeWidthPx
        strokeCap = Paint.Cap.ROUND
        color = ContextCompat.getColor(context, trackColor)
    }
    canvas.drawCircle(centerX, centerY, radius, trackPaint)

    if (progress > 0f) {
        val progressPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = strokeWidthPx
            strokeCap = Paint.Cap.ROUND
            color = ContextCompat.getColor(context, progressColor)
        }

        val sweepAngle = 360f * progress
        canvas.drawArc(
            RectF(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
            ),
            -90f,
            -sweepAngle,
            false,
            progressPaint
        )
    }

    return bitmap
}
