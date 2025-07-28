package com.example.presentation.features.auth.target

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthStateManager
import com.example.domain.model.UserActivityLevel
import com.example.domain.model.Gender
import com.example.domain.model.Goal
import com.example.domain.model.User
import com.example.domain.repository.FirebaseAuthRepository
import com.example.domain.repository.UserRepository
import com.example.presentation.arch.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TargetVM @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: FirebaseAuthRepository,
    private val authStateManager: AuthStateManager,
) : BaseViewModel() {
    private val maxSteps = 8

    private var totalStep by mutableIntStateOf(6)

    private var step by mutableIntStateOf(0)

    private var goal by mutableStateOf<Goal?>(null)

    private var weightChange by mutableFloatStateOf(0f)

    private var gender by mutableStateOf<Gender?>(null)

    private var userActivityLevel by mutableStateOf<UserActivityLevel?>(null)

    private var currentWeight by mutableFloatStateOf(0f)

    private var height by mutableIntStateOf(0)

    private var birthDate by mutableStateOf<LocalDate?>(null)

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
        )

    fun onBackPressed() {
        if (step == 0) {
            goToAuth()
        } else {
            backToPreviousStep()
        }
    }

    private fun backToPreviousStep() {
        step = when {
            step == 3 && goal == Goal.MAINTAIN -> 1
            else -> step - 1
        }
    }

    private fun goToAuth(){
        viewModelScope.launch {
            authRepository.signOut()
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
        if (step == maxSteps) saveUserInfo(context)
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
            handleLoading(
                isLoading = true
            )
            clearErrors()

            try {
                val userId = authRepository.getCurrentUserId()

                val user = User(
                    id = userId!!,
                    goal = goal!!,
                    weightChange = weightChange,
                    gender = gender!!,
                    userActivityLevel = userActivityLevel!!,
                    currentWeight = currentWeight,
                    height = height,
                    birthDate = birthDate,
                )

                val calculatedCalories = user.calculateCalories()
                val userWithCalories = user.copy(targetCalories = calculatedCalories ?: 0)

                userRepository.updateUserInfo(userWithCalories).onSuccess {
                    authStateManager.updateFullyRegistered(true)
                }.onFailure { exception ->
                    handleUnexpectedError(exception, context)
                }
            } catch (e: Exception) {
                handleUnexpectedError(e, context)
            } finally {
                handleLoading(
                    isLoading = false
                )
            }
        }
    }

    private fun isDataValid(): Boolean {
        return goal != null &&
                gender != null &&
                userActivityLevel != null &&
                currentWeight > 0 &&
                height > 0 &&
                birthDate != null &&
                (goal != Goal.LOSE && goal != Goal.GAIN || weightChange > 0)
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
)
