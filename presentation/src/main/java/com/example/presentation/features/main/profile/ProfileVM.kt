package com.example.presentation.features.main.profile

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.domain.manager.AuthStateManager
import com.example.domain.model.user.Gender
import com.example.domain.model.user.Goal
import com.example.domain.model.user.UserActivityLevel
import com.example.domain.model.user.isGoalAchieved
import com.example.domain.usecase.auth.SignOutUseCase
import com.example.domain.usecase.user.GetCurrentUserUseCase
import com.example.domain.usecase.user.UpdateUserInfoUseCase
import com.example.presentation.R
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.features.auth.onboarding.OnboardingVM.Companion.MAX_HEIGHT
import com.example.presentation.features.auth.onboarding.OnboardingVM.Companion.MAX_WEIGHT
import com.example.presentation.features.auth.onboarding.OnboardingVM.Companion.MAX_WEIGHT_CHANGE
import com.example.presentation.features.auth.onboarding.OnboardingVM.Companion.MIN_HEIGHT
import com.example.presentation.features.auth.onboarding.OnboardingVM.Companion.MIN_WEIGHT
import com.example.presentation.features.auth.onboarding.OnboardingVM.Companion.MIN_WEIGHT_CHANGE
import com.example.presentation.features.auth.onboarding.models.InputValidation
import com.example.presentation.widget.WidgetEventNotifier
import com.example.presentation.features.main.profile.models.ProfileEditDialogType
import com.example.presentation.features.main.profile.models.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class ProfileVM @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val updateUserInfoUseCase: UpdateUserInfoUseCase,
    private val authStateManager: AuthStateManager,
    @ApplicationContext private val context: Context,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            handleLoading(true)
            getCurrentUserUseCase()
                .onSuccess { user ->
                    _uiState.update { it.copy(user = user) }
                    handleLoading(false)
                }
                .onFailure { error ->
                    handleError(error, context) { loadUserProfile() }
                }
        }
    }

    fun onEditClick(dialogType: ProfileEditDialogType){
        val currentUser = _uiState.value.user

        val tempWeightChange = if (dialogType == ProfileEditDialogType.WEIGHT_CHANGE) {
            val targetWeight = currentUser?.targetWeight
            val currentWeight = currentUser?.currentWeight
            if (targetWeight != null && currentWeight != null)
                abs(currentWeight - targetWeight).toString() else ""
        } else ""

        _uiState.update { state ->
            state.copy(
                editDialogType = dialogType,
                tempGender = currentUser?.gender,
                tempGoal = currentUser?.goal,
                tempActivityLevel = currentUser?.userActivityLevel,
                tempWeightChange = tempWeightChange,
                tempCurrentWeight = currentUser?.currentWeight?.toString() ?: "",
                tempHeight = currentUser?.height?.toString() ?: "",
                tempBirthDate = currentUser?.birthDate,
                tempCaloriesGoal = currentUser?.targetCalories?.toString() ?: ""
            )
        }
    }

    fun saveDialogChanges() {
        val state = _uiState.value
        val currentUser = state.user ?: return

        val currentDialogType = state.editDialogType
        val oldUserGoal = currentUser.goal

        viewModelScope.launch {
            val updatedUser = when (currentDialogType) {
                ProfileEditDialogType.GENDER -> currentUser.copy(gender = state.tempGender!!)
                ProfileEditDialogType.GOAL -> {
                    when (val newGoal = state.tempGoal!!) {
                        Goal.MAINTAIN -> {
                            currentUser.copy(goal = newGoal, targetWeight = null)
                        }

                        Goal.LOSE, Goal.GAIN -> {
                            val initialTargetWeight =
                                if (newGoal != oldUserGoal) currentUser.currentWeight
                                else currentUser.targetWeight ?: currentUser.currentWeight
                            currentUser.copy(goal = newGoal, targetWeight = initialTargetWeight)
                        }
                    }
                }
                ProfileEditDialogType.ACTIVITY_LEVEL -> currentUser.copy(userActivityLevel = state.tempActivityLevel!!)
                ProfileEditDialogType.WEIGHT_CHANGE -> {
                    val weightChange = state.tempWeightChange.toFloatOrNull()
                    val currentWeight = currentUser.currentWeight
                    val validation = validateWeightChange(weightChange)

                    _uiState.update { it.copy(validation = validation) }

                    if (validation.isValid && currentWeight != null) {
                        val calculatedTargetWeight = when (currentUser.goal) {
                            Goal.LOSE -> currentWeight - weightChange!!
                            Goal.GAIN -> currentWeight + weightChange!!
                            Goal.MAINTAIN -> currentWeight
                        }
                        currentUser.copy(targetWeight = calculatedTargetWeight)
                    } else {
                        currentUser
                    }
                }
                ProfileEditDialogType.CURRENT_WEIGHT -> {
                    val weight = state.tempCurrentWeight.toFloatOrNull()

                    val validation = validateWeight(weight)
                    _uiState.update { it.copy(validation = validation) }

                    if (validation.isValid) currentUser.copy(currentWeight = weight)
                    else currentUser
                }
                ProfileEditDialogType.HEIGHT -> {
                    val height = state.tempHeight.toIntOrNull()

                    val validation = validateHeight(height)
                    _uiState.update { it.copy(validation = validation) }

                    if (validation.isValid) currentUser.copy(height = height)
                    else currentUser
                }
                ProfileEditDialogType.DATE_OF_BIRTH -> currentUser.copy(birthDate = state.tempBirthDate)
                ProfileEditDialogType.CALORIES_GOAL -> {
                    val calories = state.tempCaloriesGoal.toIntOrNull()?: 0
                    currentUser.copy(targetCalories = calories)
                }
                null -> currentUser
            }

            if(_uiState.value.validation.isValid) {
                handleLoading(true)
                updateUserInfoUseCase(updatedUser)
                    .onSuccess {
                        _uiState.update { it.copy(user = updatedUser, editDialogType = null) }

                        val newGoal = state.tempGoal

                        if (currentDialogType == ProfileEditDialogType.GOAL &&
                            (newGoal == Goal.LOSE || newGoal == Goal.GAIN) &&
                            (oldUserGoal != newGoal)
                        ) {
                            _uiState.update { it.copy(editDialogType = ProfileEditDialogType.WEIGHT_CHANGE) }
                        }

                        if (updatedUser.isGoalAchieved() && oldUserGoal == newGoal)
                            _uiState.update { it.copy(showInfoDialog = true) }

                        handleLoading(false)
                    }
                    .onFailure { error ->
                        handleError(error, context) { saveDialogChanges() }
                        onDialogDismiss()
                    }
            }
        }
    }

    private fun validateWeight(value: Float?): InputValidation {
        return when {
            value == null || value <= 0 -> InputValidation(
                isValid = false,
                errorMessage = null
            )
            value < MIN_WEIGHT -> InputValidation(
                isValid = false,
                errorMessage = context.getString(R.string.validation_min_weight, MIN_WEIGHT)
            )
            value > MAX_WEIGHT -> InputValidation(
                isValid = false,
                errorMessage = context.getString(R.string.validation_max_weight, MAX_WEIGHT)
            )
            else -> InputValidation(isValid = true, errorMessage = null)
        }
    }

    private fun validateHeight(value: Int?): InputValidation {
        return when {
            value == null || value <= 0 -> InputValidation(
                isValid = false,
                errorMessage = null
            )
            value < MIN_HEIGHT -> InputValidation(
                isValid = false,
                errorMessage = context.getString(R.string.validation_min_height, MIN_HEIGHT)
            )
            value > MAX_HEIGHT -> InputValidation(
                isValid = false,
                errorMessage = context.getString(R.string.validation_max_height, MAX_HEIGHT)
            )
            else -> InputValidation(isValid = true, errorMessage = null)
        }
    }

    private fun validateWeightChange(value: Float?): InputValidation {
        return when {
            value == null || value < 0 -> InputValidation(
                isValid = false,
                errorMessage = null
            )
            value < MIN_WEIGHT_CHANGE -> InputValidation(
                isValid = false,
                errorMessage = context.getString(
                    R.string.validation_min_weight_change,
                    MIN_WEIGHT_CHANGE
                )
            )
            value > MAX_WEIGHT_CHANGE -> InputValidation(
                isValid = false,
                errorMessage = context.getString(
                    R.string.validation_max_weight_change,
                    MAX_WEIGHT_CHANGE
                )
            )
            else -> InputValidation(isValid = true, errorMessage = null)
        }
    }

    fun onDialogDismiss() {
        _uiState.update { it.copy(editDialogType = null, showInfoDialog = false, validation = InputValidation()) }
    }

    fun updateTempGender(gender: Gender) {
        _uiState.update { it.copy(tempGender = gender) }
    }

    fun updateTempGoal(goal: Goal) {
        _uiState.update { it.copy(tempGoal = goal) }
    }

    fun updateTempActivityLevel(activityLevel: UserActivityLevel) {
        _uiState.update { it.copy(tempActivityLevel = activityLevel) }
    }

    fun updateTempWeightChange(weight: String) {
        _uiState.update { it.copy(tempWeightChange = weight, validation = InputValidation()) }
    }

    fun updateTempCurrentWeight(weight: String) {
        _uiState.update { it.copy(tempCurrentWeight = weight, validation = InputValidation()) }
    }

    fun updateTempHeight(height: String) {
        _uiState.update { it.copy(tempHeight = height, validation = InputValidation()) }
    }

    fun updateTempBirthDate(date: LocalDate) {
        _uiState.update { it.copy(tempBirthDate = date) }
    }

    fun updateTempCaloriesGoal(calories: String) {
        _uiState.update { it.copy(tempCaloriesGoal = calories) }
    }

    fun onLogoutClick() {
        _uiState.update { it.copy(showLogoutDialog = true) }
    }

    fun onLogoutConfirmation(status: Boolean) {
        _uiState.update { it.copy(showLogoutDialog = false) }
        if(status)
            viewModelScope.launch {
                signOutUseCase.invoke()

                WidgetEventNotifier.notifyAuthChanged(context)

                authStateManager.setAuthState(isLoggedIn = false, isFullyRegistered = false)
            }
    }
}