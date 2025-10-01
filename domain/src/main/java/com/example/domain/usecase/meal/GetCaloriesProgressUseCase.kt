package com.example.domain.usecase.meal

import com.example.domain.model.diary.CaloriesProgress
import com.example.domain.repository.MealRepository
import com.example.domain.repository.UserRepository
import javax.inject.Inject
class GetCaloriesProgressUseCase @Inject constructor(
    private val mealRepository: MealRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String, date: String): Result<CaloriesProgress> =
        runCatching {
            val dailyNutrition = mealRepository.getDailyNutrition(userId, date).getOrThrow()
            val targetCalories = userRepository.getTargetCalories(userId).getOrThrow()
            CaloriesProgress(
                consumed = dailyNutrition.calories,
                target = targetCalories
            )
        }
}