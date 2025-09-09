package com.example.domain.extension

import com.example.domain.model.diary.DailyMeals
import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.MealNutritionData
import com.example.domain.model.diary.NutritionData

fun DailyMeals.calculateDayNutrition(): NutritionData {
    val allDishes = this.breakfast + this.lunch + this.dinner + this.snacks

    return allDishes.fold(NutritionData()) { total, dish ->
        NutritionData(
            calories = total.calories + dish.kcal,
            carb = total.carb + dish.carb,
            protein = total.protein + dish.protein,
            fat = total.fat + dish.fats
        )
    }
}

fun DailyMeals.getTotalNutrition(): NutritionData {
    val mealNutrition = this.calculateMealNutrition()
    return NutritionData(
        calories = mealNutrition.breakfast.calories + mealNutrition.lunch.calories +
                mealNutrition.dinner.calories + mealNutrition.snacks.calories,
        carb = mealNutrition.breakfast.carb + mealNutrition.lunch.carb +
                mealNutrition.dinner.carb + mealNutrition.snacks.carb,
        protein = mealNutrition.breakfast.protein + mealNutrition.lunch.protein +
                mealNutrition.dinner.protein + mealNutrition.snacks.protein,
        fat = mealNutrition.breakfast.fat + mealNutrition.lunch.fat +
                mealNutrition.dinner.fat + mealNutrition.snacks.fat
    )
}

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