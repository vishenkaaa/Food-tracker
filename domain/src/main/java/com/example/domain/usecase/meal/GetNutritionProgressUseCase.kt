package com.example.domain.usecase.meal

import com.example.domain.extension.calculateDayNutrition
import com.example.domain.model.diary.NutritionProgress
import com.example.domain.repository.MealRepository
import com.example.domain.repository.UserRepository
import javax.inject.Inject

class GetNutritionProgressUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(userId: String, date: String): Result<NutritionProgress> =
        runCatching {
            val user = userRepository.getUser(userId).getOrNull()

            val macroNutrients = user?.calculateMacroNutrients()

            val targetCarbs = macroNutrients?.carbs ?: 0f
            val targetProtein = macroNutrients?.proteins ?: 0f
            val targetFat = macroNutrients?.fats ?: 0f

            val dailyMeals = mealRepository.getMealsByDate(userId, date).getOrThrow()
            val dayNutrition = dailyMeals.calculateDayNutrition()

            NutritionProgress(
                targetFat = targetFat,
                targetProtein = targetProtein,
                targetCarb = targetCarbs,
                consumedFat = dayNutrition.fat,
                consumedProtein = dayNutrition.protein,
                consumedCarb = dayNutrition.carb
            )
        }
}