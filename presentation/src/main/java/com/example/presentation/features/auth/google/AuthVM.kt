package com.example.presentation.features.auth.google

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.GoogleCredentialManager
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import com.example.domain.usecase.auth.SignInWithGoogleUseCase
import com.example.presentation.features.main.MainVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthVM @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val credentialManager: GoogleCredentialManager,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    fun signInWithGoogle(context: Context, mainVM: MainVM, forceNewAccount: Boolean = true, onNewSignIn: () -> Unit) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val idToken = credentialManager.getGoogleIdToken(context, forceNewAccount)
                val result = signInWithGoogleUseCase(idToken)

                _state.value = if (result.isSuccess) {
                    val user = result.getOrThrow()
                    if (user.isNew) {
                        userRepository.createUser(user)
                    }

                    mainVM.setAuthState(true, user.targetCalories != 0, user.id)

                    if (user.isNew) {
                        AuthState.NewUser(user)
                    } else AuthState.Success(user)
                } else {
                    AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }

                if(result.isSuccess) onNewSignIn()
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class NewUser(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}
