package com.example.domain.model.statistics

import java.time.DayOfWeek
import java.time.LocalDate

data class DayStatistics(
    val date: LocalDate,
    val calories: Int,
    val carbs: Int = 0,
    val protein: Int = 0,
    val fat: Int = 0
)

data class WeeklyNutritionStatistics(
    val targetCalories: Int,
    val dayStatistics: List<DayStatistics>,
    val weekStart: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY),
    val averageCalories: Int = 0,
    val averageCarbs: Int = 0,
    val averageProtein: Int = 0,
    val averageFat: Int = 0,
    val maxCalories: Int = 0,
    val maxCarbs: Int = 0,
    val maxProtein: Int = 0,
    val maxFat: Int = 0
)