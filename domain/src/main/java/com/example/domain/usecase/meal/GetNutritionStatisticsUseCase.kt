package com.example.domain.usecase.meal

import com.example.domain.extension.calculateMealNutrition
import com.example.domain.extension.getTotalNutrition
import com.example.domain.model.diary.DailyMeals
import com.example.domain.model.diary.MealType
import com.example.domain.model.statistics.DailyNutritionStatistics
import com.example.domain.model.statistics.DayStatistics
import com.example.domain.model.statistics.MealStatistics
import com.example.domain.model.statistics.NutritionStatistics
import com.example.domain.model.statistics.StatisticsPeriod
import com.example.domain.model.statistics.WeeklyNutritionStatistics
import com.example.domain.usecase.user.GetTargetCaloriesUseCase
import java.time.LocalDate
import javax.inject.Inject

class GetNutritionStatisticsUseCase @Inject constructor(
    private val getMealsForDateUseCase: GetMealsForDateUseCase,
    private val getTargetCaloriesUseCase: GetTargetCaloriesUseCase
) {
    suspend operator fun invoke(
        userId: String,
        period: StatisticsPeriod,
        weekStart: LocalDate? = null
    ): Result<NutritionStatistics> {
        return try {
            val targetCaloriesResult = getTargetCaloriesUseCase(userId)

            if (targetCaloriesResult.isFailure) {
                return Result.failure(targetCaloriesResult.exceptionOrNull()!!)
            }

            val targetCalories = targetCaloriesResult.getOrThrow()

            when (period) {
                StatisticsPeriod.TODAY -> {
                    val todayDate = LocalDate.now().toString()
                    getDailyStatistics(userId, todayDate, targetCalories)
                }
                StatisticsPeriod.YESTERDAY -> {
                    val yesterdayDate = LocalDate.now().minusDays(1).toString()
                    getDailyStatistics(userId, yesterdayDate, targetCalories)
                }
                StatisticsPeriod.WEEK -> {
                    getWeeklyStatistics(userId, weekStart!!, targetCalories)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getWeeklyStatistics(
        userId: String,
        weekStart: LocalDate,
        targetCalories: Int
    ): Result<NutritionStatistics> {
        return try {
            val dayStatistics = mutableListOf<DayStatistics>()

            for (i in 0..6) {
                val currentDate = weekStart.plusDays(i.toLong())
                val mealsResult = getMealsForDateUseCase(userId, currentDate.toString())

                val totalCalories = if (mealsResult.isSuccess) {
                    val dailyMeals = mealsResult.getOrThrow()
                    calculateDayCalories(dailyMeals)
                } else 0

                dayStatistics.add(
                    DayStatistics(
                        date = currentDate,
                        calories = totalCalories
                    )
                )
            }

            val weeklyStatistics = WeeklyNutritionStatistics(
                targetCalories = targetCalories,
                dayStatistics = dayStatistics,
                weekStart = weekStart
            )

            Result.success(NutritionStatistics.Weekly(weeklyStatistics))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun calculateDayCalories(dailyMeals: DailyMeals): Int {
        val allDishes = dailyMeals.breakfast + dailyMeals.lunch + dailyMeals.dinner + dailyMeals.snacks
        return allDishes.sumOf { it.kcal }
    }

    private suspend fun getDailyStatistics(
        userId: String,
        date: String,
        targetCalories: Int
    ): Result<NutritionStatistics> {
        val mealsResult = getMealsForDateUseCase(userId, date)

        if (mealsResult.isFailure) {
            return Result.failure(mealsResult.exceptionOrNull()!!)
        }

        val dailyMeals = mealsResult.getOrThrow()
        val mealNutrition = dailyMeals.calculateMealNutrition()
        val totalNutrition = dailyMeals.getTotalNutrition()

        val totalCalories = totalNutrition.calories

        val mealStats = listOf(
            MealStatistics(
                mealType = MealType.BREAKFAST,
                calories = mealNutrition.breakfast.calories,
                carbs = mealNutrition.breakfast.carb,
                protein = mealNutrition.breakfast.protein,
                fat = mealNutrition.breakfast.fat,
                percentage = if (totalCalories > 0) mealNutrition.breakfast.calories.toFloat() / totalCalories else 0f
            ),
            MealStatistics(
                mealType = MealType.LUNCH,
                calories = mealNutrition.lunch.calories,
                carbs = mealNutrition.lunch.carb,
                protein = mealNutrition.lunch.protein,
                fat = mealNutrition.lunch.fat,
                percentage = if (totalCalories > 0) mealNutrition.lunch.calories.toFloat() / totalCalories else 0f
            ),
            MealStatistics(
                mealType = MealType.DINNER,
                calories = mealNutrition.dinner.calories,
                carbs = mealNutrition.dinner.carb,
                protein = mealNutrition.dinner.protein,
                fat = mealNutrition.dinner.fat,
                percentage = if (totalCalories > 0) mealNutrition.dinner.calories.toFloat() / totalCalories else 0f
            ),
            MealStatistics(
                mealType = MealType.SNACKS,
                calories = mealNutrition.snacks.calories,
                carbs = mealNutrition.snacks.carb,
                protein = mealNutrition.snacks.protein,
                fat = mealNutrition.snacks.fat,
                percentage = if (totalCalories > 0) mealNutrition.snacks.calories.toFloat() / totalCalories else 0f
            )
        )

        val progress = if (targetCalories > 0) (totalCalories.toFloat() / targetCalories).coerceAtMost(1f) else 0f

        val dailyStats = DailyNutritionStatistics(
            totalCalories = totalCalories,
            targetCalories = targetCalories,
            progress = progress,
            mealStatistics = mealStats
        )

        return Result.success(NutritionStatistics.Daily(dailyStats))
    }
}