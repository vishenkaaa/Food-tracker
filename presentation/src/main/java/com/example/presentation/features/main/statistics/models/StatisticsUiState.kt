package com.example.presentation.features.main.statistics.models

import com.example.domain.model.statistics.DailyNutritionStatistics
import com.example.domain.model.statistics.StatisticsPeriod
import com.example.domain.model.statistics.WeeklyNutritionStatistics
import java.time.DayOfWeek
import java.time.LocalDate

data class StatisticsUiState(
    val selectedPeriod: StatisticsPeriod = StatisticsPeriod.TODAY,
    val todayStatistics: DailyNutritionStatistics? = null,
    val yesterdayStatistics: DailyNutritionStatistics? = null,
    val weeklyStatistics: WeeklyNutritionStatistics? = null,
    val weekStart: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY)
)