package com.example.presentation.features.main.statistics

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.domain.model.statistics.NutritionStatistics
import com.example.domain.model.statistics.StatisticsPeriod
import com.example.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.domain.usecase.meal.GetNutritionStatisticsUseCase
import com.example.presentation.R
import com.example.presentation.arch.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StatisticsVM @Inject constructor(
    private val getNutritionStatisticsUseCase: GetNutritionStatisticsUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    @ApplicationContext private val context: Context,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    fun onPeriodSelected(period: StatisticsPeriod) {
        _uiState.update { it.copy(selectedPeriod = period) }
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            handleLoading(true)

            val currentUserId = getCurrentUserIdUseCase()
            if (currentUserId == null){
                handleError(Exception(context.getString(R.string.user_not_authenticated))) {
                    loadStatistics()
                }
                return@launch
            }

            val result = getNutritionStatisticsUseCase(
                userId = currentUserId,
                period = _uiState.value.selectedPeriod,
                weekStart = if (_uiState.value.selectedPeriod == StatisticsPeriod.WEEK) {
                    _uiState.value.weekStart
                } else null
            )

            result.fold(
                onSuccess = { statistics ->
                    _uiState.update {
                        it.copy(nutritionStatistics = statistics)
                    }
                    handleLoading(false)
                },
                onFailure = { exception ->
                    handleError(exception) {
                        loadStatistics()
                    }
                }
            )
        }
    }

    fun onPreviousWeek() {
        val newWeekStart = _uiState.value.weekStart.minusWeeks(1)
        _uiState.update { it.copy(weekStart = newWeekStart) }
        loadStatistics()
    }

    fun onNextWeek() {
        val newWeekStart = _uiState.value.weekStart.plusWeeks(1)
        _uiState.update { it.copy(weekStart = newWeekStart) }
        loadStatistics()
    }
}

data class StatisticsUiState(
    val selectedPeriod: StatisticsPeriod = StatisticsPeriod.TODAY,
    val nutritionStatistics: NutritionStatistics? = null,
    val weekStart: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY)
)