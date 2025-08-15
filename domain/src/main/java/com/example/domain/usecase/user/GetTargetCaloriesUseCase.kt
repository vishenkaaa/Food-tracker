package com.example.domain.usecase.user

import com.example.domain.repository.UserRepository
import javax.inject.Inject

class GetTargetCaloriesUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(userId: String): Result<Int> {
        return userRepository.getTargetCalories(userId)
    }
}