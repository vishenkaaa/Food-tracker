package com.example.presentation.features.main.diary

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.domain.extension.calculateDayNutrition
import com.example.domain.extension.calculateMealNutrition
import com.example.domain.model.diary.DailyMeals
import com.example.domain.model.diary.MealType
import com.example.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.domain.usecase.camera.CheckCameraPermissionUseCase
import com.example.domain.usecase.meal.GetMealsForDateRangeUseCase
import com.example.domain.usecase.user.GetTargetCaloriesUseCase
import com.example.domain.usecase.user.MigrateUserDataUseCase
import com.example.presentation.R
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.features.main.diary.extensions.getMealsForDate
import com.example.presentation.features.main.diary.models.CameraPermissionState
import com.example.presentation.features.main.diary.models.DiaryScreenUIState
import com.example.presentation.features.main.diary.models.NavigationDirection
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
    @param:ApplicationContext private val context: Context,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getTargetCaloriesUseCase: GetTargetCaloriesUseCase,
    private val getMealsForDateRangeUseCase: GetMealsForDateRangeUseCase,
    private val checkCameraPermissionUseCase: CheckCameraPermissionUseCase,
    migrateUserDataUseCase: MigrateUserDataUseCase,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(DiaryScreenUIState())
    val uiState: StateFlow<DiaryScreenUIState> = _uiState.asStateFlow()

    private val _cameraPermissionState = MutableStateFlow(CameraPermissionState())
    val cameraPermissionState: StateFlow<CameraPermissionState> = _cameraPermissionState.asStateFlow()

    private var currentTargetCalories: Int = 0

    init {
        loadInitialData()

        viewModelScope.launch {
            try {
                val userId = getCurrentUserIdUseCase()
                    ?: throw Exception(context.getString(R.string.user_not_authenticated))
                val migrationResult = migrateUserDataUseCase(userId)

                if (migrationResult.isFailure) migrationResult.getOrThrow()

            } catch (_: Exception){
                handleError(Exception(context.getString(R.string.failed_to_update_user_data_please_try_restarting_the_app)), context)
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            handleLoading(true)
            try {
                val userId = getCurrentUserIdUseCase() ?: throw Exception(context.getString(R.string.user_not_authenticated))
                loadTargetCalories(userId)
                loadWeekData(userId, _uiState.value.weekStart)
                calculateSelectedDateNutrition(_uiState.value.selectedDate)
            } catch (e: Exception) {
                handleError(Exception(context.getString(R.string.error_loading_data)), context) { loadInitialData() }
                Log.e("DiaryVM", "loadInitialData: ", e)
            } finally {
                handleLoading(false)
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            handleLoading(true)
            try {
                val userId = getCurrentUserIdUseCase() ?: return@launch
                loadWeekData(userId, _uiState.value.weekStart)
                calculateSelectedDateNutrition(_uiState.value.selectedDate)
            } catch (e: Exception) {
                handleError(Exception(context.getString(R.string.error_loading_data)), context){
                    refreshData()
                }
                Log.e("DiaryVM", "refreshData: ", e)
            } finally {
                handleLoading(false)
            }
        }
    }

    fun onAddMealClick(mealType: MealType) {
        checkCameraPermission()

        if (_cameraPermissionState.value.permanentlyDenied) {
            showCameraPermissionDeniedDialog()
        } else if (_cameraPermissionState.value.hasPermission) {
            _cameraPermissionState.update { it.copy(pendingMealType = mealType) }
        } else {
            requestCameraPermissions(mealType)
        }
    }

    private fun showCameraPermissionDeniedDialog() {
        _cameraPermissionState.update { it.copy(showPermanentlyDeniedDialog = true) }
    }

    fun hideCameraPermissionDeniedDialog() {
        _cameraPermissionState.update {
            it.copy(
                showPermanentlyDeniedDialog = false,
                pendingMealType = null
            )
        }
    }

    private fun checkCameraPermission() {
        val granted = checkCameraPermissionUseCase.hasCameraPermission() &&
                checkCameraPermissionUseCase.isCameraAvailable()
        _cameraPermissionState.update {
            it.copy(
                hasPermission = granted,
                permanentlyDenied = if (granted) false else it.permanentlyDenied
            )
        }
    }

    private fun requestCameraPermissions(mealType: MealType) {
        _cameraPermissionState.update {
            it.copy(
                shouldRequest = true,
                pendingMealType = mealType
            )
        }
    }

    fun onCameraPermissionResult(granted: Boolean, permanentlyDenied: Boolean) {
        val available = checkCameraPermissionUseCase.isCameraAvailable()
        _cameraPermissionState.update {
            it.copy(
                shouldRequest = false,
                hasPermission = granted && available,
                permanentlyDenied = permanentlyDenied
            )
        }
        if (!granted) {
            clearPendingNavigation()
        }
    }

    fun resetCameraPermissionRequest() {
        _cameraPermissionState.update { it.copy(shouldRequest = false) }
    }

    fun clearPendingNavigation() {
        _cameraPermissionState.update { it.copy(pendingMealType = null) }
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        calculateSelectedDateNutrition(date)
    }

    fun onPreviousWeek() {
        val newWeekStart = _uiState.value.weekStart.minusWeeks(1)
        _uiState.update { it.copy(weekStart = newWeekStart) }
        loadNewWeek(newWeekStart, NavigationDirection.BACKWARD) { onPreviousWeek() }
    }

    fun onNextWeek() {
        val newWeekStart = _uiState.value.weekStart.plusWeeks(1)
        _uiState.update { it.copy(weekStart = newWeekStart) }
        loadNewWeek(newWeekStart, NavigationDirection.FORWARD) { onNextWeek() }
    }

    private fun loadNewWeek(
        weekStart: LocalDate,
        direction: NavigationDirection,
        retryAction: () -> Unit
    ) {
        viewModelScope.launch {
            handleLoading(true)
            try {
                val selectedDate = _uiState.value.selectedDate
                val newSelectedDate = if (isDateInWeek(selectedDate, weekStart)) {
                    selectedDate
                } else {
                    when (direction) {
                        NavigationDirection.FORWARD -> weekStart
                        NavigationDirection.BACKWARD -> weekStart.plusDays(6)
                    }
                }

                _uiState.update { it.copy(selectedDate = newSelectedDate) }

                val userId = getCurrentUserIdUseCase() ?: throw Exception(context.getString(R.string.user_not_authenticated))
                loadWeekData(userId, weekStart)

                calculateSelectedDateNutrition(newSelectedDate)
            } catch (e: Exception) {
                handleError(Exception(context.getString(R.string.error_loading_data)), context) { retryAction() }
                Log.e("DiaryVM", "loadNewWeek: ", e)
            } finally {
                handleLoading(false)
            }
        }
    }

    private suspend fun loadTargetCalories(userId: String): Result<Unit> {
        getTargetCaloriesUseCase(userId).fold(
            onSuccess = { calories ->
                currentTargetCalories = calories
                _uiState.update { it.copy(caloriesTarget = calories) }
                return Result.success(Unit)
            },
            onFailure = { e ->
                Log.e("DiaryVM", "loadTargetCalories: ", e)
                return Result.failure(e)
            }
        )
    }

    private suspend fun loadWeekData(userId: String, weekStart: LocalDate): Result<Unit> {
        val weekEnd = weekStart.plusDays(6)
        val startDateStr = weekStart.toString()
        val endDateStr = weekEnd.toString()

        getMealsForDateRangeUseCase(userId, startDateStr, endDateStr).fold(
            onSuccess = { weekMealsList ->
                _uiState.update { it.copy(weekMeals = weekMealsList) }
                return Result.success(Unit)
            },
            onFailure = { e ->
                Log.e("DiaryVM", "loadWeekData: ", e)
                return Result.failure(e)
            }
        )
    }

    private fun calculateSelectedDateNutrition(selectedDate: LocalDate) {
        val dailyMeals = _uiState.value.getMealsForDate(selectedDate) ?: DailyMeals(date = selectedDate)

        val dayNutrition = dailyMeals.calculateDayNutrition()
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
}