package com.example.domain.usecase.meal

import com.example.domain.model.diary.DailyMeals
import com.example.domain.repository.MealRepository
import javax.inject.Inject

class GetMealsForDateUseCase @Inject constructor(
    private val mealRepository: MealRepository,
) {
    suspend operator fun invoke(userId: String, date: String):  Result<DailyMeals> {
        return mealRepository.getMealsByDate(userId, date)
    }
}