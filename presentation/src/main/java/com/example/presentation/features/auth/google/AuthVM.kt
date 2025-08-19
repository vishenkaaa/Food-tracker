package com.example.presentation.features.auth.google

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.domain.manager.AuthStateManager
import com.example.domain.model.user.User
import com.example.domain.usecase.auth.GetGoogleIdTokenUseCase
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
    private val getGoogleIdTokenUseCase: GetGoogleIdTokenUseCase,
    private val authStateManager: AuthStateManager
) : BaseViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    fun signInWithGoogle(context: Context, forceNewAccount: Boolean = true) {
        viewModelScope.launch {
            clearErrors()
            handleLoading(true)

            try {
                val idToken = getGoogleIdTokenUseCase(forceNewAccount)
                val result = signInWithGoogleUseCase(idToken)

                if (result.isSuccess) {
                    val user = result.getOrThrow()

                    authStateManager.setAuthState(true, user.targetCalories != 0)

                    _state.value = if (user.isNew) {
                        AuthState.NewUser(user)
                    } else {
                        AuthState.Success(user)
                    }
                } else {
                    val exception = result.exceptionOrNull() ?: Exception(context.getString(R.string.unknown_error))
                    handleError(exception)
                    _state.value = AuthState.Error(exception.message ?: context.getString(R.string.unknown_error))
                }
            } catch (e: Exception) {
                handleError(e, context)
                _state.value = AuthState.Error(e.message ?: context.getString(R.string.unknown_error))
            } finally {
                handleLoading(false)
            }
        }
    }
}

sealed class AuthState {
    data object Idle : AuthState()
    data class Success(val user: User) : AuthState()
    data class NewUser(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}
