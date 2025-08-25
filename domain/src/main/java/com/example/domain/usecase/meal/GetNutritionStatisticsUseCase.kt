package com.example.domain.usecase.meal

import com.example.domain.extension.calculateDayNutrition
import com.example.domain.extension.calculateMealNutrition
import com.example.domain.extension.getTotalNutrition
import com.example.domain.extension.roundTo1Decimal
import com.example.domain.model.diary.MealType
import com.example.domain.model.diary.NutritionData
import com.example.domain.model.statistics.DailyNutritionStatistics
import com.example.domain.model.statistics.DayStatistics
import com.example.domain.model.statistics.MealStatistics
import com.example.domain.model.statistics.NutritionStatistics
import com.example.domain.model.statistics.StatisticsPeriod
import com.example.domain.model.statistics.WeeklyNutritionStatistics
import com.example.domain.usecase.user.GetTargetCaloriesUseCase
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.roundToInt

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

                val dayNutrition = if (mealsResult.isSuccess) {
                    val dailyMeals = mealsResult.getOrThrow()
                    dailyMeals.calculateDayNutrition()
                } else {
                    NutritionData(0, 0f, 0f, 0f)
                }

                dayStatistics.add(
                    DayStatistics(
                        date = currentDate,
                        calories = dayNutrition.calories,
                        carbs = dayNutrition.carb,
                        protein = dayNutrition.protein,
                        fat = dayNutrition.fat
                    )
                )
            }

            val averageCalories = dayStatistics.map { it.calories }.average().toInt()
            val averageCarbs = dayStatistics.map { it.carbs }.average().toFloat().roundTo1Decimal()
            val averageProtein = dayStatistics.map { it.protein }.average().toFloat().roundTo1Decimal()
            val averageFat = dayStatistics.map { it.fat }.average().toFloat().roundTo1Decimal()

            val maxCalories = dayStatistics.maxOfOrNull { it.calories } ?: 0
            val maxCarbs = dayStatistics.maxOfOrNull { it.carbs } ?: 0f
            val maxProtein = dayStatistics.maxOfOrNull { it.protein } ?: 0f
            val maxFat = dayStatistics.maxOfOrNull { it.fat } ?: 0f

            val weeklyStatistics = WeeklyNutritionStatistics(
                targetCalories = targetCalories,
                dayStatistics = dayStatistics,
                weekStart = weekStart,
                averageCalories = averageCalories,
                averageCarbs = averageCarbs,
                averageProtein = averageProtein,
                averageFat = averageFat,
                maxCalories = maxCalories,
                maxCarbs = maxCarbs,
                maxProtein = maxProtein,
                maxFat = maxFat
            )

            Result.success(NutritionStatistics.Weekly(weeklyStatistics))
        } catch (e: Exception) {
            Result.failure(e)
        }
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

        val calorieValues = listOf(
            mealNutrition.breakfast.calories.toFloat(),
            mealNutrition.lunch.calories.toFloat(),
            mealNutrition.dinner.calories.toFloat(),
            mealNutrition.snacks.calories.toFloat()
        )

        val correctedPercentages = calculateCorrectedPercentages(
            calorieValues,
            totalCalories.toFloat()
        )

        val mealStats = listOf(
            MealStatistics(
                mealType = MealType.BREAKFAST,
                calories = mealNutrition.breakfast.calories,
                carbs = mealNutrition.breakfast.carb,
                protein = mealNutrition.breakfast.protein,
                fat = mealNutrition.breakfast.fat,
                percentage = correctedPercentages[0]
            ),
            MealStatistics(
                mealType = MealType.LUNCH,
                calories = mealNutrition.lunch.calories,
                carbs = mealNutrition.lunch.carb,
                protein = mealNutrition.lunch.protein,
                fat = mealNutrition.lunch.fat,
                percentage = correctedPercentages[1]
            ),
            MealStatistics(
                mealType = MealType.DINNER,
                calories = mealNutrition.dinner.calories,
                carbs = mealNutrition.dinner.carb,
                protein = mealNutrition.dinner.protein,
                fat = mealNutrition.dinner.fat,
                percentage = correctedPercentages[2]
            ),
            MealStatistics(
                mealType = MealType.SNACKS,
                calories = mealNutrition.snacks.calories,
                carbs = mealNutrition.snacks.carb,
                protein = mealNutrition.snacks.protein,
                fat = mealNutrition.snacks.fat,
                percentage = correctedPercentages[3]
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

    private fun calculateCorrectedPercentages(values: List<Float>, total: Float): List<Int> {
        if (total <= 0 || values.all { it <= 0 }) return List(values.size) { 0 }

        val exactPercentages = values.map { (it / total * 100) }

        val roundedPercentages = exactPercentages.map { it.roundToInt() }.toMutableList()
        val currentSum = roundedPercentages.sum()

        if (currentSum == 100) return roundedPercentages

        val difference = 100 - currentSum

        val remainders = exactPercentages.mapIndexed { index, exact ->
            index to (exact - roundedPercentages[index])
        }

        val sortedByRemainders = if (difference > 0) {
            remainders.sortedByDescending { it.second }.map { it.first }
        } else {
            remainders.sortedBy { it.second }.map { it.first }
        }

        for (i in 0 until abs(difference)) {
            val indexToAdjust = sortedByRemainders[i % sortedByRemainders.size]
            if (difference > 0) {
                roundedPercentages[indexToAdjust] += 1
            } else {
                roundedPercentages[indexToAdjust] -= 1
            }
        }

        return roundedPercentages
    }
}