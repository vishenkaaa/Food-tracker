package com.example.presentation.features.auth.onboarding.models

import com.example.domain.model.user.MacroNutrients
import com.example.domain.model.user.Gender
import com.example.domain.model.user.Goal
import com.example.domain.model.user.UserActivityLevel
import java.time.LocalDate

data class OnboardingUiState(
    val step: Int = 0,
    val goal: Goal? = null,
    val currentWeight: String = "",
    val weightChange: String = "",
    val height: String = "",
    val gender: Gender? = null,
    val birthDate: LocalDate? = null,
    val activityLevel: UserActivityLevel? = null,
    val targetCalories: Int = 0,
    val bmi: Float = 0f,
    val macroNutrients: MacroNutrients = MacroNutrients(),
    val isNextEnabled: Boolean = true,
    val showLogoutDialog: Boolean = false,
    val weightValidation: InputValidation = InputValidation(),
    val heightValidation: InputValidation = InputValidation(),
    val weightChangeValidation: InputValidation = InputValidation()
)