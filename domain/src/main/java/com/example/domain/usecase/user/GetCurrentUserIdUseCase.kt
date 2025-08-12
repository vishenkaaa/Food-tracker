package com.example.domain.usecase.user

import com.example.domain.model.user.User
import com.example.domain.repository.FirebaseAuthRepository
import com.example.domain.repository.UserRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository
) {
    suspend operator fun invoke(): Result<User> {
        return try {
            val currentUserId = firebaseAuthRepository.getCurrentUserId()
                ?: return Result.failure(Exception("User is not authenticated"))

            userRepository.getUser(currentUserId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}