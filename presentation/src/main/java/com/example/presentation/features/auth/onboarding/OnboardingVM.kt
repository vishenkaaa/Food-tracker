package com.example.presentation.features.auth.onboarding

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.domain.manager.AuthStateManager
import com.example.domain.model.user.Gender
import com.example.domain.model.user.Goal
import com.example.domain.model.user.MacroNutrients
import com.example.domain.model.user.User
import com.example.domain.model.user.UserActivityLevel
import com.example.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.domain.usecase.auth.SignOutUseCase
import com.example.domain.usecase.user.UpdateUserInfoUseCase
import com.example.presentation.R
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.common.utils.BMICalculator
import com.example.presentation.features.auth.onboarding.models.InputValidation
import com.example.presentation.features.auth.onboarding.models.OnboardingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class OnboardingVM @Inject constructor(
    private val authStateManager: AuthStateManager,
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val updateUserInfoUseCase: UpdateUserInfoUseCase,
    @param:ApplicationContext private val context: Context
) : BaseViewModel() {
    companion object {
        const val MAX_STEPS = 9
        const val WELCOME_STEP = 0

        const val MIN_NAME = 2
        const val MIN_WEIGHT = 30f
        const val MAX_WEIGHT = 500f
        const val MIN_HEIGHT = 100
        const val MAX_HEIGHT = 250
        const val MIN_WEIGHT_CHANGE = 0.1f
        const val MAX_WEIGHT_CHANGE = 100f
    }

    private var user by mutableStateOf(User())

    private var step by mutableIntStateOf(0)
    private var name by mutableStateOf("")
    private var goal by mutableStateOf<Goal?>(null)
    private var weightChange by mutableStateOf("")
    private var gender by mutableStateOf<Gender?>(null)
    private var userActivityLevel by mutableStateOf<UserActivityLevel?>(null)
    private var currentWeight by mutableStateOf("")
    private var height by mutableStateOf("")
    private var birthDate by mutableStateOf<LocalDate?>(null)
    private var targetCalories by mutableIntStateOf(0)

    private var nameValidation by mutableStateOf(InputValidation())
    private var weightValidation by mutableStateOf(InputValidation())
    private var heightValidation by mutableStateOf(InputValidation())
    private var weightChangeValidation by mutableStateOf(InputValidation())

    val uiState: OnboardingUiState
        get() = OnboardingUiState(
            step = step,
            name = name,
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
            showLogoutDialog = showLogoutDialog.value,
            nameValidation = nameValidation,
            weightValidation = weightValidation,
            heightValidation = heightValidation,
            weightChangeValidation = weightChangeValidation
        )

    private val isNextEnabled: Boolean
        get() = when (step) {
            0 -> true
            1 -> nameValidation.isValid
            2 -> goal != null
            3 -> goal == Goal.MAINTAIN || weightChangeValidation.isValid
            4 -> weightValidation.isValid
            5 -> heightValidation.isValid
            6 -> gender != null
            7 -> birthDate != null
            8 -> userActivityLevel != null
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
            listOf(0, 1, 2, 4, 5, 6, 7, 8, 9)
        } else {
            listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
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
        if(isNextEnabled) {
            val validSteps = getValidStepsForGoal()
            val currentIndex = validSteps.indexOf(step)

            if (currentIndex >= 0 && currentIndex < validSteps.size - 1) {
                step = validSteps[currentIndex + 1]
            }

            if (step == MAX_STEPS) {
                saveUserInfo(context)
            }
        }
    }

    fun onNameSelected(value: String) {
        name = value
        nameValidation = validateName(value)
    }

    fun onGoalSelected(value: Goal) {
        goal = value
        if (value == Goal.MAINTAIN) {
            onWeightChangeSelected("0")
        }
    }

    fun onWeightChangeSelected(value: String) {
        weightChange = value
        weightChangeValidation = validateWeightChange(value)
    }

    fun onCurrentWeightSelected(value: String) {
        currentWeight = value
        weightValidation = validateWeight(value)
    }

    fun onHeightSelected(value: String) {
        height = value
        heightValidation = validateHeight(value)
    }

    fun onGenderSelected(value: Gender) {
        gender = value
    }

    fun onActivityLevelSelected(value: UserActivityLevel) {
        userActivityLevel = value
    }

    fun onBirthDateSelected(value: LocalDate) {
        birthDate = value
    }

    private fun validateName(value: String): InputValidation {
        return when {
            value.trim().length < MIN_NAME -> InputValidation(
                isValid = false,
                errorMessage = context.getString(R.string.validation_min_name, MIN_NAME)
            )
            else -> InputValidation(isValid = true, errorMessage = null)
        }
    }

    private fun validateWeight(value: String): InputValidation {
        val weight = value.toFloatOrNull()
        return when {
            weight == null || weight <= 0 -> InputValidation(
                isValid = false,
                errorMessage = null
            )
            weight < MIN_WEIGHT -> InputValidation(
                isValid = false,
                errorMessage = context.getString(R.string.validation_min_weight, MIN_WEIGHT)
            )
            weight > MAX_WEIGHT -> InputValidation(
                isValid = false,
                errorMessage = context.getString(R.string.validation_max_weight, MAX_WEIGHT)
            )
            else -> InputValidation(isValid = true, errorMessage = null)
        }
    }

    private fun validateHeight(value: String): InputValidation {
        val heightValue = value.toIntOrNull()
        return when {
            heightValue == null || heightValue <= 0 -> InputValidation(
                isValid = false,
                errorMessage = null
            )
            heightValue < MIN_HEIGHT -> InputValidation(
                isValid = false,
                errorMessage = context.getString(R.string.validation_min_height, MIN_HEIGHT)
            )
            heightValue > MAX_HEIGHT -> InputValidation(
                isValid = false,
                errorMessage = context.getString(R.string.validation_max_height, MAX_HEIGHT)
            )
            else -> InputValidation(isValid = true, errorMessage = null)
        }
    }

    private fun validateWeightChange(value: String): InputValidation {
        val change = value.toFloatOrNull()
        return when {
            change == null || change < 0 -> InputValidation(
                isValid = false,
                errorMessage = null
            )
            change < MIN_WEIGHT_CHANGE -> InputValidation(
                isValid = false,
                errorMessage = context.getString(
                    R.string.validation_min_weight_change,
                    MIN_WEIGHT_CHANGE
                )
            ) 
            change > MAX_WEIGHT_CHANGE -> InputValidation(
                isValid = false, 
                errorMessage = context.getString(
                    R.string.validation_max_weight_change,
                    MAX_WEIGHT_CHANGE
                )
            )
            else -> InputValidation(isValid = true, errorMessage = null)
        }
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
                        handleError(exception, context)
                    }
            } catch (e: Exception) {
                handleError(e, context)
            } finally {
                handleLoading(isLoading = false)
            }
        }
    }

    private fun createUserWithCalculations(userId: String): User {
        val weightChangeFloat = weightChange.toFloatOrNull() ?: 0f
        val currentWeightFloat = currentWeight.toFloatOrNull() ?: 0f
        val heightInt = height.toIntOrNull() ?: 0

        val targetWeight = when(goal){
            Goal.LOSE -> currentWeightFloat - weightChangeFloat
            Goal.GAIN -> currentWeightFloat + weightChangeFloat
            else -> null
        }

        user = User(
            id = userId,
            name = name.trim(),
            goal = goal!!,
            targetWeight = targetWeight,
            gender = gender!!,
            userActivityLevel = userActivityLevel!!,
            currentWeight = currentWeightFloat,
            height = heightInt,
            birthDate = birthDate,
            isNew = false
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