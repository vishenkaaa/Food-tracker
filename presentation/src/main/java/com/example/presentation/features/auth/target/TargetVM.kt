package com.example.presentation.features.auth.target

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.domain.manager.AuthStateManager
import com.example.domain.model.Gender
import com.example.domain.model.Goal
import com.example.domain.model.MacroNutrients
import com.example.domain.model.User
import com.example.domain.model.UserActivityLevel
import com.example.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.domain.usecase.auth.SignOutUseCase
import com.example.domain.usecase.user.UpdateUserInfoUseCase
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.common.utils.BMICalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TargetVM @Inject constructor(
    private val authStateManager: AuthStateManager,
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val updateUserInfoUseCase: UpdateUserInfoUseCase
) : BaseViewModel() {
    companion object {
        private const val MAX_STEPS = 8
        private const val WELCOME_STEP = 0
    }

    private var user by mutableStateOf(User())

    private var step by mutableIntStateOf(0)
    private var totalStep by mutableIntStateOf(6)

    private var goal by mutableStateOf<Goal?>(null)
    private var weightChange by mutableFloatStateOf(0f)
    private var gender by mutableStateOf<Gender?>(null)
    private var userActivityLevel by mutableStateOf<UserActivityLevel?>(null)
    private var currentWeight by mutableFloatStateOf(0f)
    private var height by mutableIntStateOf(0)
    private var birthDate by mutableStateOf<LocalDate?>(null)

    private var targetCalories by mutableIntStateOf(0)

    val uiState: TargetUiState
        get() = TargetUiState(
            step = step,
            totalSteps = totalStep,
            goal = goal,
            currentWeight = currentWeight,
            weightChange = weightChange,
            height = height,
            gender = gender,
            birthDate = birthDate,
            activityLevel = userActivityLevel,
            targetCalories = targetCalories,
            bmi = bmi,
            macroNutrients = macroNutrients
        )

    private val bmi: Float
        get() = BMICalculator.calculateBMI(currentWeight, height)

    private val macroNutrients: MacroNutrients
        get() = user.calculateMacroNutrients()

    fun onBackPressed() {
        if (step == WELCOME_STEP) {
            goToAuth()
        } else {
            navigateToPreviousStep()
        }
    }

    private fun navigateToPreviousStep() {
        step = when {
            step == 3 && goal == Goal.MAINTAIN -> 1
            else -> step - 1
        }
    }

    private fun goToAuth(){
        viewModelScope.launch {
            signOutUseCase()
        }
        authStateManager.setAuthState(isLoggedIn = false, isFullyRegistered = false)
    }

    fun onNextStep(context: Context){
        step = when {
            step == 1 && goal == Goal.MAINTAIN -> {
                onWeightChangeSelected(0f)
                3
            }
            else -> step + 1
        }
        if (step == MAX_STEPS) saveUserInfo(context)
    }

    fun onGoalSelected(value: Goal) {
        goal = value
        totalStep = when (value){
            Goal.LOSE -> 7
            Goal.MAINTAIN -> 6
            Goal.GAIN -> 7
        }
    }

    fun onWeightChangeSelected(value: Float) {
        weightChange = value
    }

    fun onGenderSelected(value: Gender) {
        gender = value
    }

    fun onActivityLevelSelected(value: UserActivityLevel) {
        userActivityLevel = value
    }

    fun onCurrentWeightSelected(value: Float) {
        currentWeight = value
    }

    fun onHeightSelected(value: Int) {
        height = value
    }

    fun onBirthDateSelected(value: LocalDate) {
        birthDate = value
    }

    fun saveUserInfo(context: Context) {
        if (!isDataValid()) {
            return
        }

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
        user = User(
            id = userId,
            goal = goal!!,
            weightChange = weightChange,
            gender = gender!!,
            userActivityLevel = userActivityLevel!!,
            currentWeight = currentWeight,
            height = height,
            birthDate = birthDate,
        )

        val calculatedCalories = user.calculateCalories() ?: 0
        return user.copy(targetCalories = calculatedCalories)
    }

    fun onFinish(){
        authStateManager.updateFullyRegistered(true)
    }

    private fun isDataValid(): Boolean {
        return goal != null &&
                gender != null &&
                userActivityLevel != null &&
                currentWeight > 0 &&
                height > 0 &&
                birthDate != null &&
                (goal == Goal.MAINTAIN || weightChange != 0f)
    }

    fun consumeError() {
        clearErrors()
    }
}

data class TargetUiState(
    val step: Int = 0,
    val totalSteps: Int = 6,
    val goal: Goal? = null,
    val currentWeight: Float = 0f,
    val weightChange: Float = 0f,
    val height: Int = 0,
    val gender: Gender? = null,
    val birthDate: LocalDate? = null,
    val activityLevel: UserActivityLevel? = null,
    val targetCalories: Int = 0,
    val bmi: Float = 0f,
    val macroNutrients: MacroNutrients = MacroNutrients()
)
