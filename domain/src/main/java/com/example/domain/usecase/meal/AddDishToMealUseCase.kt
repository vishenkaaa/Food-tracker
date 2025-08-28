package com.example.domain.usecase.meal

import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.MealType
import com.example.domain.repository.MealRepository
import javax.inject.Inject

class AddDishToMealUseCase @Inject constructor (
    private val mealRepository: MealRepository,
) {
    suspend operator fun invoke(userId: String, date: String, mealType: MealType, dish: Dish):Result<Unit>{
        return mealRepository.addDishToMeal(userId, date, mealType, dish)
    }
}