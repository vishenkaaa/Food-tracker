package com.example.domain.model.statistics

sealed class NutritionStatistics {
    data class Daily(val data: DailyNutritionStatistics) : NutritionStatistics()
    data class Weekly(val data: WeeklyNutritionStatistics) : NutritionStatistics()
}