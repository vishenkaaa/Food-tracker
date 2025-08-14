package com.example.presentation.features.auth.onboarding

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.domain.manager.AuthStateManager
import com.example.domain.model.user.Gender
import com.example.domain.model.user.Goal
import com.example.domain.model.MacroNutrients
import com.example.domain.model.user.User
import com.example.domain.model.user.UserActivityLevel
import com.example.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.domain.usecase.auth.SignOutUseCase
import com.example.domain.usecase.user.UpdateUserInfoUseCase
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.common.utils.BMICalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.example.presentation.features.auth.onboarding.models.*
import com.example.presentation.features.auth.onboarding.models.OnboardingUiState

@OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class OnboardingVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authStateManager: AuthStateManager,
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val updateUserInfoUseCase: UpdateUserInfoUseCase
) : BaseViewModel() {
    companion object {
        const val MAX_STEPS = 8
        const val WELCOME_STEP = 0

        private const val KEY_STEP = "step"
        private const val KEY_GOAL = "goal"
        private const val KEY_WEIGHT_CHANGE = "weight_change"
        private const val KEY_GENDER = "gender"
        private const val KEY_ACTIVITY_LEVEL = "activity_level"
        private const val KEY_CURRENT_WEIGHT = "current_weight"
        private const val KEY_HEIGHT = "height"
        private const val KEY_BIRTH_DATE = "birth_date"
        private const val KEY_TARGET_CALORIES = "target_calories"
    }

    private var user by mutableStateOf(User())

    private var step by savedStateHandle.saveable(KEY_STEP) { mutableIntStateOf(0) }
    private var goal by savedStateHandle.saveable(KEY_GOAL, GoalSaver) { mutableStateOf<Goal?>(null) }
    private var weightChange by savedStateHandle.saveable(KEY_WEIGHT_CHANGE) { mutableStateOf("") }
    private var gender by savedStateHandle.saveable(KEY_GENDER, GenderSaver) { mutableStateOf<Gender?>(null) }
    private var userActivityLevel by savedStateHandle.saveable(KEY_ACTIVITY_LEVEL, UserActivityLevelSaver) { mutableStateOf<UserActivityLevel?>(null) }
    private var currentWeight by savedStateHandle.saveable(KEY_CURRENT_WEIGHT) { mutableStateOf("") }
    private var height by savedStateHandle.saveable(KEY_HEIGHT) { mutableStateOf("") }
    private var birthDate by savedStateHandle.saveable(KEY_BIRTH_DATE, LocalDateSaver) { mutableStateOf<LocalDate?>(null) }
    private var targetCalories by savedStateHandle.saveable(KEY_TARGET_CALORIES) { mutableIntStateOf(0) }

    val uiState: OnboardingUiState
        get() = OnboardingUiState(
            step = step,
            goal = goal,
            currentWeight = currentWeight,
            weightChange = weightChange,
            height = height,
            gender = gender,
            birthDate = birthDate,
            activityLevel = userActivityLevel,
            targetCalories = targetCalories,
            bmi = bmi,
            macroNutrients = macroNutrients,
            isNextEnabled = isNextEnabled,
            showLogoutDialog = showLogoutDialog.value
        )

    private val isNextEnabled: Boolean
        get() = when (step) {
            0 -> true
            1 -> goal != null
            2 -> goal == Goal.MAINTAIN || (weightChange.toFloatOrNull()?.let { it > 0 } == true)
            3 -> currentWeight.toFloatOrNull()?.let { it > 0 } == true
            4 -> height.toIntOrNull()?.let { it > 0 } == true
            5 -> gender != null
            6 -> birthDate != null
            7 -> userActivityLevel != null
            else -> true
        }

    private val bmi: Float
        get() {
            val weight = currentWeight.toFloatOrNull() ?: 0f
            val heightValue = height.toIntOrNull() ?: 0
            return BMICalculator.calculateBMI(weight, heightValue)
        }

    private val macroNutrients: MacroNutrients
        get() = user.calculateMacroNutrients()

    private val showLogoutDialog = mutableStateOf(false)

    private fun requestLogoutConfirmation() {
        showLogoutDialog.value = true
    }

    fun onLogoutConfirmationResult(status: Boolean) {
        showLogoutDialog.value = false
        if (status) goToAuth()
    }

    private fun getValidStepsForGoal(): List<Int> {
        return if (goal == Goal.MAINTAIN) {
            listOf(0, 1, 3, 4, 5, 6, 7, 8)
        } else {
            listOf(0, 1, 2, 3, 4, 5, 6, 7, 8)
        }
    }

    fun onBackPressed() {
        if (step == WELCOME_STEP) {
            requestLogoutConfirmation()
        } else {
            navigateToPreviousStep()
        }
    }

    private fun navigateToPreviousStep() {
        val validSteps = getValidStepsForGoal()
        val currentIndex = validSteps.indexOf(step)

        if (currentIndex > 0) {
            step = validSteps[currentIndex - 1]
        }
    }

    private fun goToAuth(){
        viewModelScope.launch {
            signOutUseCase()
        }
        authStateManager.setAuthState(isLoggedIn = false, isFullyRegistered = false)
    }

    fun onNextStep(context: Context) {
        val validSteps = getValidStepsForGoal()
        val currentIndex = validSteps.indexOf(step)

        if (currentIndex >= 0 && currentIndex < validSteps.size - 1) {
            step = validSteps[currentIndex + 1]
        }

        if (step == MAX_STEPS) {
            saveUserInfo(context)
        }
    }

    fun onGoalSelected(value: Goal) {
        goal = value
        if (value == Goal.MAINTAIN) {
            onWeightChangeSelected("0")
        }
    }

    fun onWeightChangeSelected(value: String) {
        weightChange = value
    }

    fun onGenderSelected(value: Gender) {
        gender = value
    }

    fun onActivityLevelSelected(value: UserActivityLevel) {
        userActivityLevel = value
    }

    fun onCurrentWeightSelected(value: String) {
        currentWeight = value
    }

    fun onHeightSelected(value: String) {
        height = value
    }

    fun onBirthDateSelected(value: LocalDate) {
        birthDate = value
    }

    fun saveUserInfo(context: Context) {
        viewModelScope.launch {
            handleLoading(isLoading = true)
            clearErrors()

            try {
                val userId = getCurrentUserIdUseCase()
                    ?: throw IllegalStateException("User not authenticated")

                val userWithCalculations = createUserWithCalculations(userId)
                targetCalories = userWithCalculations.targetCalories

                updateUserInfoUseCase.invoke(userWithCalculations)
                    .onFailure { exception ->
                        handleUnexpectedError(exception, context)
                    }
            } catch (e: Exception) {
                handleUnexpectedError(e, context)
            } finally {
                handleLoading(isLoading = false)
            }
        }
    }

    private fun createUserWithCalculations(userId: String): User {
        val weightChangeFloat = weightChange.toFloatOrNull() ?: 0f
        val finalWeightChangeFloat = if(goal == Goal.LOSE) -weightChangeFloat else weightChangeFloat
        val currentWeightFloat = currentWeight.toFloatOrNull() ?: 0f
        val heightInt = height.toIntOrNull() ?: 0

        user = User(
            id = userId,
            goal = goal!!,
            weightChange = finalWeightChangeFloat,
            gender = gender!!,
            userActivityLevel = userActivityLevel!!,
            currentWeight = currentWeightFloat,
            height = heightInt,
            birthDate = birthDate,
        )

        val calculatedCalories = user.calculateCalories() ?: 0
        return user.copy(targetCalories = calculatedCalories)
    }

    fun onFinish(){
        authStateManager.updateFullyRegistered(true)
    }

    fun consumeError() {
        clearErrors()
    }
}