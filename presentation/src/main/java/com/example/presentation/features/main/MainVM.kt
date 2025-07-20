package com.example.presentation.features.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.repository.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {

    private val _userAuthState = MutableStateFlow(UserAuthState(isLoading = true))
    val userAuthState: StateFlow<UserAuthState> = _userAuthState.asStateFlow()

    fun setAuthState(isLoggedIn: Boolean?, isFullyRegistered: Boolean, identity: String?) {
        _userAuthState.value = UserAuthState(
            isLoading = false,
            isLoggedIn = isLoggedIn,
            isFullyRegistered = isFullyRegistered,
        )
    }

    fun checkUserState() {
        viewModelScope.launch {
            val isLoggedIn = authRepository.isUserLoggedIn()
            val userId = authRepository.getCurrentUserId()

            if (!isLoggedIn || userId == null) {
                setAuthState(isLoggedIn = false, isFullyRegistered = false, identity = null)
                return@launch
            }

            val isFullyRegistered = authRepository.isUserFullyRegistered(userId)
            setAuthState(isLoggedIn = true, isFullyRegistered = isFullyRegistered, identity = userId)
        }
    }

}

data class UserAuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean? = null,
    val isFullyRegistered: Boolean = false,
)