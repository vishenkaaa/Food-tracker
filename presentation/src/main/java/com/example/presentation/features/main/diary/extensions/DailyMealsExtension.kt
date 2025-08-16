package com.example.presentation.features.main.diary.extensions

import com.example.domain.model.diary.DailyMeals
import com.example.domain.model.diary.Dish
import com.example.presentation.features.main.diary.models.MealNutritionData
import com.example.presentation.features.main.diary.models.NutritionData

fun DailyMeals.calculateMealNutrition(): MealNutritionData {
    return MealNutritionData(
        breakfast = this.breakfast.calculateMealNutritionForDishes(),
        lunch = this.lunch.calculateMealNutritionForDishes(),
        dinner = this.dinner.calculateMealNutritionForDishes(),
        snacks = this.snacks.calculateMealNutritionForDishes()
    )
}

fun List<Dish>.calculateMealNutritionForDishes(): NutritionData {
    return this.fold(NutritionData()) { total, dish ->
        NutritionData(
            calories = total.calories + dish.kcal,
            carb = total.carb + dish.carb,
            protein = total.protein + dish.protein,
            fat = total.fat + dish.fats
        )
    }
}