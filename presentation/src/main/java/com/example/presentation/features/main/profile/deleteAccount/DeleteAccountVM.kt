package com.example.presentation.features.main.profile.deleteAccount

import androidx.lifecycle.viewModelScope
import com.example.domain.manager.AuthStateManager
import com.example.domain.usecase.auth.DeleteAccountUseCase
import com.example.presentation.arch.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAccountVM @Inject constructor(
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val authStateManager: AuthStateManager
) : BaseViewModel() {
    private val _showToast = MutableSharedFlow<Unit>()
    val showToast = _showToast.asSharedFlow()

    fun onDelete() {
        viewModelScope.launch {
            handleLoading(true)
            deleteAccountUseCase()
                .onSuccess {
                    authStateManager.setAuthState(isLoggedIn = false, isFullyRegistered = false)
                    _showToast.emit(Unit)
                    handleLoading(false)
                }.onFailure { error ->
                    handleUnexpectedError(error)
                }
        }
    }

    fun consumeError() {
        clearErrors()
    }
}