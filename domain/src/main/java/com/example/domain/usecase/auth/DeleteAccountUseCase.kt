package com.example.domain.usecase.auth

import com.example.domain.repository.FirebaseAuthRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        return firebaseAuthRepository.deleteAccount()
    }
}