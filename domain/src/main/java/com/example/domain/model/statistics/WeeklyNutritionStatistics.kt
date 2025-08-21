package com.example.domain.model.statistics

import java.time.DayOfWeek
import java.time.LocalDate

data class DayStatistics(
    val date: LocalDate,
    val calories: Int,
)

data class WeeklyNutritionStatistics(
    val targetCalories: Int,
    val dayStatistics: List<DayStatistics>,
    val weekStart: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY)
)