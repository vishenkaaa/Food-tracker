package com.example.presentation.common.utils

import android.content.Context
import com.example.presentation.features.auth.onboarding.models.BMIStatus
import com.example.presentation.extensions.BMICategory
import com.example.presentation.extensions.toBMICategory

object BMICalculator {
    private const val MIN_BMI = 15f
    private const val MAX_BMI = 40f

    fun calculateBMI(weightKg: Float, heightCm: Int): Float {
        return if (weightKg > 0 && heightCm > 0) {
            val heightM = heightCm / 100f
            weightKg / (heightM * heightM)
        } else {
            0f
        }
    }

    fun getBMIProgress(bmi: Float): Float {
        val clampedBMI = bmi.coerceIn(MIN_BMI, MAX_BMI)
        return (clampedBMI - MIN_BMI) / (MAX_BMI - MIN_BMI)
    }

    fun getBMICategory(bmi: Float, context: Context): BMICategory {
        val status = BMIStatus.entries.first { bmi < it.maxValue }
        return status.toBMICategory(context)
    }
}