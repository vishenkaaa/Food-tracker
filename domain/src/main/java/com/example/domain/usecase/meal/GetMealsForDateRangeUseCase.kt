package com.example.domain.usecase.meal

import com.example.domain.model.diary.DailyMeals
import com.example.domain.repository.MealRepository
import java.time.LocalDate
import javax.inject.Inject

class GetMealsForDateRangeUseCase @Inject constructor(
    private val mealRepository: MealRepository,
) {
    suspend operator fun invoke(userId: String, startDate: String, endDate: String): Result<List<DailyMeals>> {
        return mealRepository.getMealsForDateRange(userId, startDate, endDate)
            .map { mealsMap ->
                generateCompleteWeekList(LocalDate.parse(startDate), mealsMap)
            }
    }

    private fun generateCompleteWeekList(weekStart: LocalDate, mealsMap: Map<String, DailyMeals>): List<DailyMeals> {
        return (0..6).map { dayOffset ->
            val date = weekStart.plusDays(dayOffset.toLong())
            val dateStr = date.toString()
            mealsMap[dateStr]?.copy(date = date) ?: DailyMeals(date = date)
        }
    }
}