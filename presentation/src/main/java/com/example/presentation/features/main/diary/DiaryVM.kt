package com.example.presentation.features.main.diary

import androidx.lifecycle.viewModelScope
import com.example.domain.manager.AuthStateManager
import com.example.domain.usecase.auth.SignOutUseCase
import com.example.presentation.arch.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryVM @Inject constructor(
    private val signOutUseCase: SignOutUseCase,
    private val authStateManager: AuthStateManager
) : BaseViewModel() {

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase.invoke()
            authStateManager.setAuthState(isLoggedIn = false, isFullyRegistered = false)
        }
    }
}
