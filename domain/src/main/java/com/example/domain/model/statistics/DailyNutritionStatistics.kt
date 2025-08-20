package com.example.domain.model.statistics

data class DailyNutritionStatistics(
    val totalCalories: Int = 0,
    val targetCalories: Int = 0,
    val progress: Float = 0f,
    val mealStatistics: List<MealStatistics> = emptyList()
)