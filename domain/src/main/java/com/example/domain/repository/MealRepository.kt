package com.example.domain.repository

import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.DailyMeals
import com.example.domain.model.diary.MealType

interface MealRepository {
    suspend fun addDishToMeal(
        userId: String,
        date: String,
        mealType: MealType,
        dish: Dish
    ): Result<Unit>

    suspend fun removeDishFromMeal(
        userId: String,
        date: String,
        mealType: MealType,
        dishId: String
    ): Result<Unit>

    suspend fun updateDishInMeal(
        userId: String,
        date: String,
        originalMealType: MealType,
        newMealType: MealType,
        dish: Dish
    ): Result<Unit>

    suspend fun getMealsByDate(userId: String, date: String): Result<DailyMeals>

    suspend fun getDishesForMeal(
        userId: String,
        date: String,
        mealType: MealType
    ): Result<List<Dish>>

    suspend fun getMealsForDateRange(
        userId: String,
        startDate: String,
        endDate: String
    ): Result<Map<String, DailyMeals>>
}
