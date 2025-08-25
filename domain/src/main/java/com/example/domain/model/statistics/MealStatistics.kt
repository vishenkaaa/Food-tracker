package com.example.domain.model.statistics

import com.example.domain.model.diary.MealType

data class MealStatistics(
    val mealType: MealType,
    val calories: Int,
    val carbs: Float,
    val protein: Float,
    val fat: Float,
    val percentage: Int
)