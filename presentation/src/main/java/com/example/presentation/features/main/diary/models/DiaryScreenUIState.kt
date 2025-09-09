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
    val carb: Float = 0f,
    val protein: Float = 0f,
    val fat: Float = 0f,

    val weekMeals: List<DailyMeals> = emptyList(),

    val breakfastNutrition: NutritionData = NutritionData(),
    val lunchNutrition: NutritionData = NutritionData(),
    val dinnerNutrition: NutritionData = NutritionData(),
    val snacksNutrition: NutritionData = NutritionData()
)