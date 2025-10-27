package com.example.domain.usecase.auth

import com.example.domain.repository.FirebaseAuthRepository
import javax.inject.Inject

class GetCurrentUserEmailUseCase @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository
) {
    suspend operator fun invoke(): String?{
        return firebaseAuthRepository.getCurrentUserEmail()
    }
}
