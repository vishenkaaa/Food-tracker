package com.example.domain.model.statistics

import com.example.domain.model.diary.MealType

data class MealStatistics(
    val mealType: MealType,
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fat: Int,
    val percentage: Float
)