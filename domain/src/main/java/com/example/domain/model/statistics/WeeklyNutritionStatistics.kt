package com.example.domain.model.statistics

import java.time.DayOfWeek
import java.time.LocalDate

data class DayStatistics(
    val date: LocalDate,
    val calories: Int,
    val carbs: Float = 0f,
    val protein: Float = 0f,
    val fat: Float = 0f
)

data class WeeklyNutritionStatistics(
    val targetCalories: Int,
    val dayStatistics: List<DayStatistics>,
    val weekStart: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY),
    val averageCalories: Int = 0,
    val averageCarbs: Float = 0f,
    val averageProtein: Float = 0f,
    val averageFat: Float = 0f,
    val maxCalories: Int = 0,
    val maxCarbs: Float = 0f,
    val maxProtein: Float = 0f,
    val maxFat: Float = 0f
)