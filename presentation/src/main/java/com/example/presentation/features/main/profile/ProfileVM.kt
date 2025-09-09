package com.example.presentation.features.main.profile

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.domain.manager.AuthStateManager
import com.example.domain.model.user.Gender
import com.example.domain.model.user.Goal
import com.example.domain.model.user.UserActivityLevel
import com.example.domain.usecase.auth.SignOutUseCase
import com.example.domain.usecase.user.GetCurrentUserUseCase
import com.example.domain.usecase.user.UpdateUserInfoUseCase
import com.example.presentation.arch.BaseViewModel
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
        _uiState.update { state ->
            state.copy(
                editDialogType = dialogType,
                tempGender = if (dialogType == ProfileEditDialogType.GENDER) currentUser?.gender else null,
                tempGoal = if (dialogType == ProfileEditDialogType.GOAL) currentUser?.goal else null,
                tempActivityLevel = if (dialogType == ProfileEditDialogType.ACTIVITY_LEVEL) currentUser?.userActivityLevel else null,
                tempWeightChange = if (dialogType == ProfileEditDialogType.WEIGHT_CHANGE) {
                    val weightChange = currentUser?.weightChange ?: 0f
                    if (weightChange != 0f) abs(weightChange).toString() else ""
                } else "",
                tempCurrentWeight = if (dialogType == ProfileEditDialogType.CURRENT_WEIGHT) (currentUser?.currentWeight).toString() else "",
                tempHeight = if (dialogType == ProfileEditDialogType.HEIGHT) (currentUser?.height).toString() else "",
                tempBirthDate = if (dialogType == ProfileEditDialogType.DATE_OF_BIRTH) currentUser?.birthDate else null,
                tempCaloriesGoal = if (dialogType == ProfileEditDialogType.CALORIES_GOAL) (currentUser?.targetCalories).toString() else ""
            )
        }
    }

    fun saveDialogChanges() {
        val state = _uiState.value
        val currentUser = state.user ?: return

        viewModelScope.launch {
            handleLoading(true)

            val updatedUser = when (state.editDialogType) {
                ProfileEditDialogType.GENDER -> currentUser.copy(gender = state.tempGender!!)
                ProfileEditDialogType.GOAL -> currentUser.copy(goal = state.tempGoal!!)
                ProfileEditDialogType.ACTIVITY_LEVEL -> currentUser.copy(userActivityLevel = state.tempActivityLevel!!)
                ProfileEditDialogType.WEIGHT_CHANGE -> {
                    val weightChange = state.tempWeightChange.toFloatOrNull()
                    if(weightChange!=null){
                        val finalWeightChange = if(currentUser.goal == Goal.LOSE)
                            -weightChange
                        else
                            weightChange
                        currentUser.copy(weightChange = finalWeightChange)
                    }else currentUser
                }
                ProfileEditDialogType.CURRENT_WEIGHT -> {
                    val weight = state.tempCurrentWeight.toFloatOrNull() ?: 0f
                    currentUser.copy(currentWeight = weight)
                }
                ProfileEditDialogType.HEIGHT -> {
                    val height = state.tempHeight.toIntOrNull() ?: 0
                    currentUser.copy(height = height)
                }
                ProfileEditDialogType.DATE_OF_BIRTH -> currentUser.copy(birthDate = state.tempBirthDate)
                ProfileEditDialogType.CALORIES_GOAL -> {
                    val calories = state.tempCaloriesGoal.toIntOrNull()?: 0
                    currentUser.copy(targetCalories = calories)
                }
                null -> currentUser
            }

            updateUserInfoUseCase(updatedUser)
                .onSuccess {
                    _uiState.update { it.copy(user = updatedUser, editDialogType = null) }
                    handleLoading(false)
                }
                .onFailure { error ->
                    handleError(error, context) { saveDialogChanges() }
                    onDialogDismiss()
                }
        }
    }

    fun onDialogDismiss() {
        _uiState.update { it.copy(editDialogType = null) }
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
        _uiState.update { it.copy(tempWeightChange = weight) }
    }

    fun updateTempCurrentWeight(weight: String) {
        _uiState.update { it.copy(tempCurrentWeight = weight) }
    }

    fun updateTempHeight(height: String) {
        _uiState.update { it.copy(tempHeight = height) }
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
                authStateManager.setAuthState(isLoggedIn = false, isFullyRegistered = false)
            }
    }
}