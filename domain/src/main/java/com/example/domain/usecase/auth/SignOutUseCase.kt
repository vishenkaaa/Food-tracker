package com.example.domain.usecase.auth

import com.example.domain.repository.FirebaseAuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository
) {
    suspend operator fun invoke(){
        firebaseAuthRepository.signOut()
    }
}