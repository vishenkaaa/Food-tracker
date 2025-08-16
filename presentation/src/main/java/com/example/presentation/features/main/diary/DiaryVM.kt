package com.example.presentation.features.main.diary

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.domain.model.diary.DailyMeals
import com.example.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.domain.usecase.meal.GetMealsForDateRangeUseCase
import com.example.domain.usecase.user.GetTargetCaloriesUseCase
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.features.main.diary.extensions.calculateMealNutrition
import com.example.presentation.features.main.diary.extensions.getMealsForDate
import com.example.presentation.features.main.diary.models.DiaryScreenUIState
import com.example.presentation.features.main.diary.models.NutritionData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DiaryVM @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getTargetCaloriesUseCase: GetTargetCaloriesUseCase,
    private val getMealsForDateRangeUseCase: GetMealsForDateRangeUseCase,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(DiaryScreenUIState())
    val uiState: StateFlow<DiaryScreenUIState> = _uiState.asStateFlow()

    private var currentTargetCalories: Int = 0

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            handleLoading(true)
            try {
                val userId = getCurrentUserIdUseCase() ?: throw Exception("User not authenticated")
                loadTargetCalories(userId)
                loadWeekData(userId, _uiState.value.weekStart)
                calculateSelectedDateNutrition(_uiState.value.selectedDate)
            } catch (e: Exception) {
                handleUnexpectedError(e, context, { loadInitialData() })
            } finally {
                handleLoading(false)
            }
        }
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        calculateSelectedDateNutrition(date)
    }

    fun onPreviousWeek() {
        val newWeekStart = _uiState.value.weekStart.minusWeeks(1)
        _uiState.update { it.copy(weekStart = newWeekStart) }
        loadNewWeek(newWeekStart) {onPreviousWeek()}
    }

    fun onNextWeek() {
        val newWeekStart = _uiState.value.weekStart.plusWeeks(1)
        _uiState.update { it.copy(weekStart = newWeekStart) }
        loadNewWeek(newWeekStart) {onNextWeek()}
    }

    private fun loadNewWeek(weekStart: LocalDate, retryAction: () -> Unit) {
        viewModelScope.launch {
            handleLoading(true)
            try {
                val userId = getCurrentUserIdUseCase() ?: throw Exception("User not authenticated")
                loadWeekData(userId, weekStart)

                val selectedDate = _uiState.value.selectedDate
                val newSelectedDate = if (isDateInWeek(selectedDate, weekStart))
                    selectedDate
                else
                    weekStart

                _uiState.update { it.copy(selectedDate = newSelectedDate) }
                calculateSelectedDateNutrition(newSelectedDate)
            } catch (e: Exception) {
                handleUnexpectedError(e, context, retryAction)
            } finally {
                handleLoading(false)
            }
        }
    }

    private suspend fun loadTargetCalories(userId: String) {
        getTargetCaloriesUseCase(userId).fold(
            onSuccess = { calories ->
                currentTargetCalories = calories
                _uiState.update { it.copy(caloriesTarget = calories) }
            },
            onFailure = { e ->
               handleUnexpectedError(e, context)
            }
        )
    }

    private suspend fun loadWeekData(userId: String, weekStart: LocalDate) {
        val weekEnd = weekStart.plusDays(6)
        val startDateStr = weekStart.toString()
        val endDateStr = weekEnd.toString()

        getMealsForDateRangeUseCase(userId, startDateStr, endDateStr).fold(
            onSuccess = { weekMealsList ->
                _uiState.update { it.copy(weekMeals = weekMealsList) }
            },
            onFailure = { e ->
                handleUnexpectedError(e, context)
            }
        )
    }

    private fun calculateSelectedDateNutrition(selectedDate: LocalDate) {
        val dailyMeals = _uiState.value.getMealsForDate(selectedDate) ?: DailyMeals(date = selectedDate)

        val dayNutrition = calculateDayNutrition(dailyMeals)
        val mealNutrition = dailyMeals.calculateMealNutrition()

        _uiState.update { currentState ->
            currentState.copy(
                caloriesConsumed = dayNutrition.calories,
                carb = dayNutrition.carb,
                protein = dayNutrition.protein,
                fat = dayNutrition.fat,
                breakfastNutrition = mealNutrition.breakfast,
                lunchNutrition = mealNutrition.lunch,
                dinnerNutrition = mealNutrition.dinner,
                snacksNutrition = mealNutrition.snacks
            )
        }
    }

    private fun isDateInWeek(date: LocalDate, weekStart: LocalDate): Boolean {
        val weekEnd = weekStart.plusDays(6)
        return !date.isBefore(weekStart) && !date.isAfter(weekEnd)
    }

    private fun calculateDayNutrition(dailyMeals: DailyMeals): NutritionData {
        val allDishes = dailyMeals.breakfast + dailyMeals.lunch + dailyMeals.dinner + dailyMeals.snacks

        return allDishes.fold(NutritionData()) { total, dish ->
            NutritionData(
                calories = total.calories + dish.kcal,
                carb = total.carb + dish.carb,
                protein = total.protein + dish.protein,
                fat = total.fat + dish.fats
            )
        }
    }
}