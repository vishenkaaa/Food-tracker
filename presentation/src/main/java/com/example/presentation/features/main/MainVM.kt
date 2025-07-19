package com.example.presentation.features.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainVM() : ViewModel() {
    private val _userAuthState = MutableStateFlow(UserAuthState(isLoading = false))
    val userAuthState: StateFlow<UserAuthState> = _userAuthState.asStateFlow()

}

data class UserAuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean? = null,
    val isDataVerified: Boolean = false,
    val isBlocked: Boolean = false,
    val isFullyRegistered: Boolean = false,
    val identity: String? = null
)