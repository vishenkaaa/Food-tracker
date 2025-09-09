package com.example.domain.usecase.auth

import com.example.domain.repository.FirebaseAuthRepository
import javax.inject.Inject

class OldGoogleSignInUseCase @Inject constructor (
    private val firebaseAuthRepository: FirebaseAuthRepository
) {
    suspend operator fun invoke(forceNewAccount: Boolean): Result<String>{
        return firebaseAuthRepository.oldSignInWithGoogle(forceNewAccount)
    }
}
