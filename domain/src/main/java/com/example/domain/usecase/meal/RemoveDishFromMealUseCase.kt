package com.example.domain.usecase.meal

import com.example.domain.model.diary.MealType
import com.example.domain.repository.MealRepository
import javax.inject.Inject

class RemoveDishFromMealUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(userId: String, date: String, mealType: MealType, dishId: String): Result<Unit> {
        return mealRepository.removeDishFromMeal(userId, date, mealType, dishId)
    }
}