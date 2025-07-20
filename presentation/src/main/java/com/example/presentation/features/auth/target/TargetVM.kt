package com.example.presentation.features.auth.target

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.repository.FirebaseAuthRepository
import com.example.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TargetVM @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {

    fun saveTargetCalories(targetCalories: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch

            userRepository.updateTargetCalories(userId, targetCalories).onSuccess {
                onSuccess()
            }
        }
    }
}
