package com.example.domain.usecase.user

import com.example.domain.manager.UserAuthState
import com.example.domain.repository.FirebaseAuthRepository
import javax.inject.Inject

class CheckUserStateUseCase @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository
) {
    suspend operator fun invoke(): UserAuthState {
        val isLoggedIn = firebaseAuthRepository.isUserLoggedIn()
        val userId = firebaseAuthRepository.getCurrentUserId()

        if (!isLoggedIn || userId == null) {
            return UserAuthState(
                isLoading = false,
                isLoggedIn = false,
                isFullyRegistered = false
            )
        }

        val isFullyRegistered = firebaseAuthRepository.isUserFullyRegistered(userId)
        return UserAuthState(
            isLoading = false,
            isLoggedIn = true,
            isFullyRegistered = isFullyRegistered
        )
    }
}