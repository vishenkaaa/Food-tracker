package com.example.presentation.features.main.diary

import com.example.presentation.arch.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class DiaryVM @Inject constructor() : BaseViewModel() {
    private val _uiState = MutableStateFlow(DiaryScreenUIState())
    val uiState: StateFlow<DiaryScreenUIState> = _uiState.asStateFlow()

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun onPreviousWeek() {
        _uiState.update { it.copy(weekStart = it.weekStart.minusWeeks(1)) }
    }

    fun onNextWeek() {
        _uiState.update { it.copy(weekStart = it.weekStart.plusWeeks(1)) }
    }
}

data class DiaryScreenUIState(
    val selectedDate: LocalDate = LocalDate.now(),
    val weekStart: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY),
    val caloriesConsumed: Int = 1285,
    val caloriesTarget: Int = 2540,
    val carbs: Int = 122,
    val protein: Int = 93,
    val fat: Int = 44
)