package com.example.domain.manager

import com.example.domain.logger.ErrorLogger
import com.example.domain.usecase.user.CheckUserStateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthStateManager @Inject constructor(
    private val checkUserStateUseCase: CheckUserStateUseCase,
    private val errorLogger: ErrorLogger
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

    suspend fun checkAndUpdateUserState() {
        try {
            _userAuthState.value = _userAuthState.value.copy(isLoading = true)
            val newState = checkUserStateUseCase()
            _userAuthState.value = newState.copy(isLoading = false)
        } catch (e: Exception) {
            errorLogger.logException(e)
            _userAuthState.value = UserAuthState(
                isLoading = false,
                isLoggedIn = false,
                isFullyRegistered = false
            )
        }
    }
}

data class UserAuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean? = null,
    val isFullyRegistered: Boolean = false,
)