package com.example.presentation.features.main.diary.models

import com.example.domain.model.diary.DailyMeals
import com.example.domain.model.diary.NutritionData
import java.time.DayOfWeek
import java.time.LocalDate

data class DiaryScreenUIState(
    val selectedDate: LocalDate = LocalDate.now(),
    val weekStart: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY),
    val caloriesConsumed: Int = 0,
    val caloriesTarget: Int = 0,
    val carb: Int = 0,
    val protein: Int = 0,
    val fat: Int = 0,

    val weekMeals: List<DailyMeals> = emptyList(),

    val breakfastNutrition: NutritionData = NutritionData(),
    val lunchNutrition: NutritionData = NutritionData(),
    val dinnerNutrition: NutritionData = NutritionData(),
    val snacksNutrition: NutritionData = NutritionData()
)