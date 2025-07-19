package com.example.presentation.features.auth

import android.content.Context
import android.credentials.CredentialManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.GoogleCredentialManager
import com.example.domain.model.User
import com.example.domain.usecase.auth.SignInWithGoogleUseCase
import com.example.domain.usecase.auth.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthVM @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val credentialManager: GoogleCredentialManager
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    fun signInWithGoogle(context: Context, forceNewAccount: Boolean = true) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val idToken = credentialManager.getGoogleIdToken(context, forceNewAccount)
                val result = signInWithGoogleUseCase(idToken)

                _state.value = if (result.isSuccess) {
                    val user = result.getOrThrow()
                    if (user.isNew) AuthState.NewUser(user) else AuthState.Success(user)
                } else {
                    AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
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
