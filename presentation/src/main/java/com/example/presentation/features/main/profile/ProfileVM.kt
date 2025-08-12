package com.example.presentation.features.main.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.domain.manager.AuthStateManager
import com.example.domain.model.user.User
import com.example.domain.usecase.auth.SignOutUseCase
import com.example.domain.usecase.user.GetCurrentUserUseCase
import com.example.presentation.arch.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileVM @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val authStateManager: AuthStateManager
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val showLogoutDialog = mutableStateOf(false)

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            handleLoading(true)
            getCurrentUserUseCase()
                .onSuccess { user ->
                    _uiState.update { it.copy(user = user) }
                    handleLoading(false)
                }
                .onFailure { error ->
                    handleUnexpectedError(error)
                }
        }
    }

    fun consumeError() {
        clearErrors()
    }

    fun onLogoutClick() {
        _uiState.update { it.copy(showLogoutDialog = true) }
    }

    fun onDeleteAccountClick() {
        // TODO: видалення акаунту
    }

    fun onLogoutConfirmation(status: Boolean) {
        _uiState.update { it.copy(showLogoutDialog = false) }
        if(status)
            viewModelScope.launch {
                signOutUseCase.invoke()
                authStateManager.setAuthState(isLoggedIn = false, isFullyRegistered = false)
            }
    }
}

data class ProfileUiState(
    val user: User? = null,
    val showLogoutDialog: Boolean = false
)