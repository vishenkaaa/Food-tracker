package com.example.domain.usecase.auth

import com.example.domain.model.user.User
import com.example.domain.repository.FirebaseAuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<User>{
        return firebaseAuthRepository.signInWithGoogle(idToken)
    }
}
