package com.example.domain.repository

import com.example.domain.model.User

interface UserRepository {
    suspend fun getUser(userId: String): Result<User>
    suspend fun updateUserInfo(user: User): Result<Unit>
    suspend fun updateTargetCalories(userId: String, targetCalories: Int): Result<Unit>
}