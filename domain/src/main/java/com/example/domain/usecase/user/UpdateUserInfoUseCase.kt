package com.example.domain.usecase.user

import com.example.domain.model.user.User
import com.example.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User): Result<Unit>{
        return userRepository.updateUserInfo(user)
    }
}
