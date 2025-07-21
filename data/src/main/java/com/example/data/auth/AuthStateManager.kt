package com.example.data.auth

import com.example.domain.repository.FirebaseAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthStateManager @Inject constructor(
    private val authRepository: FirebaseAuthRepository
) {
    private val _userAuthState = MutableStateFlow(UserAuthState(isLoading = true))
    val userAuthState: StateFlow<UserAuthState> = _userAuthState.asStateFlow()

    fun setAuthState(isLoggedIn: Boolean?, isFullyRegistered: Boolean) {
        _userAuthState.value = UserAuthState(
            isLoading = false,
            isLoggedIn = isLoggedIn,
            isFullyRegistered = isFullyRegistered,
        )
    }

    fun updateFullyRegistered(isFullyRegistered: Boolean) {
        val currentState = _userAuthState.value
        _userAuthState.value = currentState.copy(
            isFullyRegistered = isFullyRegistered
        )
    }

    suspend fun checkUserState() {
        val isLoggedIn = authRepository.isUserLoggedIn()
        val userId = authRepository.getCurrentUserId()

        if (!isLoggedIn || userId == null) {
            setAuthState(isLoggedIn = false, isFullyRegistered = false)
            return
        }

        val isFullyRegistered = authRepository.isUserFullyRegistered(userId)
        setAuthState(isLoggedIn = true, isFullyRegistered = isFullyRegistered)
    }
}

data class UserAuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean? = null,
    val isFullyRegistered: Boolean = false,
)