package com.example.presentation.features.main.profile.models

import com.example.domain.model.user.Gender
import com.example.domain.model.user.Goal
import com.example.domain.model.user.User
import com.example.domain.model.user.UserActivityLevel
import com.example.presentation.features.auth.onboarding.models.InputValidation
import java.time.LocalDate

data class ProfileUiState(
    val user: User? = null,
    val showLogoutDialog: Boolean = false,
    val showInfoDialog: Boolean = false,
    val editDialogType: ProfileEditDialogType? = null,

    val tempGender: Gender? = null,
    val tempGoal: Goal? = null,
    val tempActivityLevel: UserActivityLevel? = null,
    val tempWeightChange: String = "",
    val tempCurrentWeight: String = "",
    val tempHeight: String = "",
    val tempBirthDate: LocalDate? = null,
    val tempCaloriesGoal: String = "",
    val validation: InputValidation = InputValidation(isValid = true),
)