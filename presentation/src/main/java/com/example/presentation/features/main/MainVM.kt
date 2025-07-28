package com.example.presentation.features.main

import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthStateManager
import com.example.data.auth.UserAuthState
import com.example.presentation.arch.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val authStateManager: AuthStateManager
) : BaseViewModel() {

    val userAuthState: StateFlow<UserAuthState> = authStateManager.userAuthState

    fun checkUserState() {
        viewModelScope.launch {
            authStateManager.checkUserState()
        }
    }
}