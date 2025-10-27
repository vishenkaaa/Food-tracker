package com.example.presentation.features.main.statistics

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.domain.model.statistics.NutritionStatistics
import com.example.domain.model.statistics.StatisticsPeriod
import com.example.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.domain.usecase.meal.GetNutritionStatisticsUseCase
import com.example.presentation.R
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.features.main.statistics.models.StatisticsUiState
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
class StatisticsVM @Inject constructor(
    private val getNutritionStatisticsUseCase: GetNutritionStatisticsUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    @ApplicationContext private val context: Context,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    private val _isDailyLoading = MutableStateFlow(false)
    val isDailyLoading: StateFlow<Boolean> = _isDailyLoading.asStateFlow()

    private val _isWeeklyLoading = MutableStateFlow(false)
    val isWeeklyLoading: StateFlow<Boolean> = _isWeeklyLoading.asStateFlow()

    init {
        loadDailyStatistics(StatisticsPeriod.TODAY)
    }

    fun onPeriodSelected(period: StatisticsPeriod) {
        _uiState.update { it.copy(selectedPeriod = period) }

        when (period) {
            StatisticsPeriod.TODAY -> {
                if (_uiState.value.todayStatistics == null) {
                    loadDailyStatistics(period)
                }
            }

            StatisticsPeriod.YESTERDAY -> {
                if (_uiState.value.yesterdayStatistics == null) {
                    loadDailyStatistics(period)
                }
            }

            StatisticsPeriod.WEEK -> {
                val currentWeekStart = _uiState.value.weekStart
                val existingWeekStart = _uiState.value.weeklyStatistics?.weekStart

                if (existingWeekStart != currentWeekStart || _uiState.value.weeklyStatistics == null) {
                    loadWeeklyStatistics()
                }
            }
        }
    }

    fun refreshAllStatistics(date: LocalDate) {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        when (date) {
            today -> loadDailyStatistics(StatisticsPeriod.TODAY)
            yesterday -> loadDailyStatistics(StatisticsPeriod.YESTERDAY)
        }

        val weekStart = uiState.value.weekStart
        val weekEnd = weekStart.plusDays(6)

        if (!date.isBefore(weekStart) && !date.isAfter(weekEnd) && _uiState.value.weeklyStatistics != null) {
            loadWeeklyStatistics()
        }
    }

    private fun loadDailyStatistics(period: StatisticsPeriod) {
        viewModelScope.launch {
            _isDailyLoading.value = true

            val currentUserId = getCurrentUserIdUseCase()
            if (currentUserId == null) {
                handleError(Exception(context.getString(R.string.user_not_authenticated))) {
                    loadDailyStatistics(period)
                }
                _isDailyLoading.value = false
                return@launch
            }

            val result = getNutritionStatisticsUseCase(
                userId = currentUserId,
                period = period,
                weekStart = null
            )

            result.fold(
                onSuccess = { statistics ->
                    if (statistics is NutritionStatistics.Daily) {
                        _uiState.update { currentState ->
                            when (period) {
                                StatisticsPeriod.TODAY -> currentState.copy(todayStatistics = statistics.data)
                                StatisticsPeriod.YESTERDAY -> currentState.copy(yesterdayStatistics = statistics.data)
                                else -> currentState
                            }
                        }
                    }
                    _isDailyLoading.value = false
                },
                onFailure = { exception ->
                    _isDailyLoading.value = false
                    handleError(exception) {
                        loadDailyStatistics(period)
                    }
                }
            )
        }
    }

    private fun loadWeeklyStatistics() {
        viewModelScope.launch {
            _isWeeklyLoading.value = true

            val currentUserId = getCurrentUserIdUseCase()
            if (currentUserId == null) {
                handleError(Exception(context.getString(R.string.user_not_authenticated))) {
                    loadWeeklyStatistics()
                }
                _isWeeklyLoading.value = false
                return@launch
            }

            val result = getNutritionStatisticsUseCase(
                userId = currentUserId,
                period = StatisticsPeriod.WEEK,
                weekStart = _uiState.value.weekStart
            )

            result.fold(
                onSuccess = { statistics ->
                    if (statistics is NutritionStatistics.Weekly) {
                        _uiState.update {
                            it.copy(weeklyStatistics = statistics.data)
                        }
                    }
                    _isWeeklyLoading.value = false
                },
                onFailure = { exception ->
                    _isWeeklyLoading.value = false
                    handleError(exception) {
                        loadWeeklyStatistics()
                    }
                }
            )
        }
    }

    fun onPreviousWeek() {
        val newWeekStart = _uiState.value.weekStart.minusWeeks(1)
        _uiState.update { it.copy(weekStart = newWeekStart) }
        loadWeeklyStatistics()
    }

    fun onNextWeek() {
        val newWeekStart = _uiState.value.weekStart.plusWeeks(1)
        _uiState.update { it.copy(weekStart = newWeekStart) }
        loadWeeklyStatistics()
    }

    fun loadStatistics() {
        when (_uiState.value.selectedPeriod) {
            StatisticsPeriod.TODAY,
            StatisticsPeriod.YESTERDAY -> {
                loadDailyStatistics(_uiState.value.selectedPeriod)
            }

            StatisticsPeriod.WEEK -> {
                loadWeeklyStatistics()
            }
        }
    }
}