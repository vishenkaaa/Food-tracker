package com.example.presentation.extensions

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.example.domain.model.BMIStatus
import com.example.presentation.R

data class BMICategory(
    val label: String,
    val color: Color
)


fun BMIStatus.toBMICategory(context: Context): BMICategory {
    return when (this) {
        BMIStatus.UNDERWEIGHT -> BMICategory(
            label = context.getString(R.string.underweight),
            color = Color(0xFF1976D2)
        )
        BMIStatus.NORMAL -> BMICategory(
            label = context.getString(R.string.normal_weight),
            color = Color(0xFF388E3C)
        )
        BMIStatus.OVERWEIGHT -> BMICategory(
            label = context.getString(R.string.overweight),
            color = Color(0xFFF57C00)
        )
        BMIStatus.OBESE -> BMICategory(
            label = context.getString(R.string.obesity),
            color = Color(0xFFD32F2F)
        )
    }
}