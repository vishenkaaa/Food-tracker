package com.example.presentation.features.auth.google

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthStateManager
import com.example.data.auth.GoogleCredentialManager
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import com.example.domain.usecase.auth.SignInWithGoogleUseCase
import com.example.presentation.R
import com.example.presentation.arch.BaseViewModel
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
    private val authStateManager: AuthStateManager
) : BaseViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    fun signInWithGoogle(context: Context, forceNewAccount: Boolean = true) {
        viewModelScope.launch {
            clearErrors()
            handleLoading(true)

            try {
                Log.d("AuthWM", "Starting Google sign-in process, forceNewAccount: $forceNewAccount")

                val idToken = credentialManager.getGoogleIdToken(context, forceNewAccount)
                val result = signInWithGoogleUseCase(idToken)

                if (result.isSuccess) {
                    val user = result.getOrThrow()
                    Log.i("AuthWM", "Google sign-in successful for user: ${user.id}, isNew: ${user.isNew}")

                    if (user.isNew) {
                        Log.d("AuthWM", "Creating new user in database")
                        userRepository.createUser(user)
                    }

                    authStateManager.setAuthState(true, user.targetCalories != 0)

                    _state.value = if (user.isNew) {
                        Log.d("AuthWM", "Navigating to onboarding for new user")
                        AuthState.NewUser(user)
                    } else {
                        Log.d("AuthWM", "User authentication completed successfully")
                        AuthState.Success(user)
                    }
                } else {
                    val exception = result.exceptionOrNull() ?: Exception(context.getString(R.string.unknown_error))
                    Log.e("AuthWM", "Google sign-in failed", exception)
                    handleUnexpectedError(exception)
                    _state.value = AuthState.Error(exception.message ?: context.getString(R.string.unknown_error))
                }
            } catch (e: Exception) {
                // Основне логування помилок
                Log.e("AuthWM", "Exception during Google sign-in", e)

                // Додаткова інформація для debugging
                Log.e("AuthWM", "Exception details: ${e.javaClass.simpleName} - ${e.message}")
                Log.e("AuthWM", "Stack trace: ${e.stackTraceToString()}")

                handleUnexpectedError(e, context)
                _state.value = AuthState.Error(e.message ?: context.getString(R.string.unknown_error))
            } finally {
                Log.d("AuthWM", "Google sign-in process completed")
                handleLoading(false)
            }
        }
    }

    fun consumeError() {
        clearErrors()
    }
}

sealed class AuthState {
    data object Idle : AuthState()
    data class Success(val user: User) : AuthState()
    data class NewUser(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}
