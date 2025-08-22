package com.example.domain.usecase.meal

import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.MealType
import com.example.domain.repository.MealRepository
import javax.inject.Inject


class UpdateDishInMealUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(
        userId: String,
        date: String,
        originalMealType: MealType,
        newMealType: MealType,
        dish: Dish
    ): Result<Unit> {
        return mealRepository.updateDishInMeal(
            userId = userId,
            date = date,
            originalMealType = originalMealType,
            newMealType = newMealType,
            dish = dish
        )
    }
}