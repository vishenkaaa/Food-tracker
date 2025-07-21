package com.example.presentation.features.auth.target

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthStateManager
import com.example.domain.repository.FirebaseAuthRepository
import com.example.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TargetVM @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: FirebaseAuthRepository,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    fun saveTargetCalories(targetCalories: Int) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch

            userRepository.updateTargetCalories(userId, targetCalories).onSuccess {
                authStateManager.updateFullyRegistered(true)
            }
        }
    }
}
