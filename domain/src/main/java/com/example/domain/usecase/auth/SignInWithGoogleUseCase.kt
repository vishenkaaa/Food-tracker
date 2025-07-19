package com.example.domain.usecase.auth

import com.example.domain.model.User
import com.example.domain.repository.FirebaseAuthRepository
import com.sun.tools.javac.util.Context
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<User>{
        return firebaseAuthRepository.signInWithGoogle(idToken)
    }
}
