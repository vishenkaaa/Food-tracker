package com.example.presentation.features.main.diary.models

import com.example.domain.model.diary.DailyMeals
import com.example.domain.model.diary.MealType
import java.time.DayOfWeek
import java.time.LocalDate

data class DiaryScreenUIState(
    val selectedDate: LocalDate = LocalDate.now(),
    val weekStart: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY),
    val caloriesConsumed: Int = 0,
    val caloriesTarget: Int = 0,
    val carbs: Int = 0,
    val protein: Int = 0,
    val fat: Int = 0,

    val weekMeals: List<DailyMeals> = emptyList(),

    val breakfastNutrition: NutritionData = NutritionData(),
    val lunchNutrition: NutritionData = NutritionData(),
    val dinnerNutrition: NutritionData = NutritionData(),
    val snacksNutrition: NutritionData = NutritionData()
)

fun DiaryScreenUIState.getNutritionForMealType(mealType: MealType): NutritionData {
    return when (mealType) {
        MealType.BREAKFAST -> breakfastNutrition
        MealType.LUNCH -> lunchNutrition
        MealType.DINNER -> dinnerNutrition
        MealType.SNACKS -> snacksNutrition
    }
}

fun DiaryScreenUIState.getMealsForDate(date: LocalDate): DailyMeals? {
    return weekMeals.find { it.date == date }
}