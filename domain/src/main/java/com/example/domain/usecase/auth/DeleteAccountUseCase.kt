package com.example.domain.usecase.auth

import com.example.domain.repository.FirebaseAuthRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val getGoogleIdTokenUseCase: GetGoogleIdTokenUseCase
) {
    suspend operator fun invoke(): Result<Unit> {
        val idTokenResult = getGoogleIdTokenUseCase(true)
        return idTokenResult.fold(
            onSuccess = { token ->
                firebaseAuthRepository.deleteAccount(token)
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}