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
import androidx.compose.runtime.saveable.Saver
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable

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
        private const val KEY_TOTAL_STEP = "total_step"
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
    private var totalStep by savedStateHandle.saveable(KEY_TOTAL_STEP) { mutableIntStateOf(6) }

    private var goal by savedStateHandle.saveable(KEY_GOAL, GoalSaver) { mutableStateOf<Goal?>(null) }
    private var weightChange by savedStateHandle.saveable(KEY_WEIGHT_CHANGE) { mutableFloatStateOf(0f) }
    private var gender by savedStateHandle.saveable(KEY_GENDER, GenderSaver) { mutableStateOf<Gender?>(null) }
    private var userActivityLevel by savedStateHandle.saveable(KEY_ACTIVITY_LEVEL, UserActivityLevelSaver) { mutableStateOf<UserActivityLevel?>(null) }
    private var currentWeight by savedStateHandle.saveable(KEY_CURRENT_WEIGHT) { mutableFloatStateOf(0f) }
    private var height by savedStateHandle.saveable(KEY_HEIGHT) { mutableIntStateOf(0) }
    private var birthDate by savedStateHandle.saveable(KEY_BIRTH_DATE, LocalDateSaver) { mutableStateOf<LocalDate?>(null) }
    private var targetCalories by savedStateHandle.saveable(KEY_TARGET_CALORIES) { mutableIntStateOf(0) }

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
            macroNutrients = macroNutrients,
            isNextEnabled = isNextEnabled
        )

    private val isNextEnabled: Boolean
        get() = when (step) {
            0 -> true
            1 -> goal != null
            2 -> goal == Goal.MAINTAIN || weightChange != 0f
            3 -> currentWeight > 0f
            4 -> height > 0
            5 -> gender != null
            6 -> birthDate != null
            7 -> userActivityLevel != null
            else -> true
        }

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
    val macroNutrients: MacroNutrients = MacroNutrients(),
    val isNextEnabled: Boolean = true
)

val GoalSaver = Saver<Goal?, String>(
    save = { it?.value },
    restore = { savedValue -> savedValue.let { Goal.fromValue(it) } }
)

val GenderSaver = Saver<Gender?, String>(
    save = { it?.value },
    restore = { savedValue -> savedValue.let { Gender.fromValue(it) } }
)

val UserActivityLevelSaver = Saver<UserActivityLevel?, String>(
    save = { it?.value },
    restore = { savedValue -> savedValue.let { UserActivityLevel.fromValue(it) } }
)

val LocalDateSaver = Saver<LocalDate?, String>(
    save = { it?.toString() },
    restore = { savedValue -> savedValue.let { LocalDate.parse(it) } }
)