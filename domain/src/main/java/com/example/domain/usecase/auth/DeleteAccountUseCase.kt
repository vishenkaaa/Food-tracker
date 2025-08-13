package com.example.domain.usecase.auth

import com.example.domain.repository.FirebaseAuthRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val getGoogleIdTokenUseCase: GetGoogleIdTokenUseCase
) {
    suspend operator fun invoke(): Result<Unit>{
        val idToken = getGoogleIdTokenUseCase(false)
        return firebaseAuthRepository.deleteAccount(idToken)
    }
}