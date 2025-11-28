package com.example.domain.model.diary

data class NutritionProgress(
    val targetProtein: Float = 0f,
    val targetCarb: Float = 0f,
    val targetFat: Float = 0f,
    val consumedProtein: Float = 0f,
    val consumedCarb: Float = 0f,
    val consumedFat: Float = 0f,
)