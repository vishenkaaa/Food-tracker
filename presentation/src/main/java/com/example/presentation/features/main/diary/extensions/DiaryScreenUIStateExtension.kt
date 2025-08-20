package com.example.presentation.features.main.diary.extensions

import com.example.domain.model.diary.DailyMeals
import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.MealType
import com.example.presentation.features.main.diary.models.DiaryScreenUIState
import com.example.domain.model.diary.NutritionData
import java.time.LocalDate

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

fun DiaryScreenUIState.getDishesForMealType(date: LocalDate, mealType: MealType): List<Dish> {
    val dailyMeals = getMealsForDate(date) ?: return emptyList()
    return when (mealType) {
        MealType.BREAKFAST -> dailyMeals.breakfast
        MealType.LUNCH -> dailyMeals.lunch
        MealType.DINNER -> dailyMeals.dinner
        MealType.SNACKS -> dailyMeals.snacks
    }
}