package com.example.domain.repository

import com.example.domain.model.user.User

interface UserRepository {
    suspend fun createUser(user: User): Result<Unit>
    suspend fun getUser(userId: String): Result<User>
    suspend fun updateUserInfo(user: User): Result<Unit>
    suspend fun isUserFullyRegistered(userId: String): Boolean
    suspend fun getTargetCalories(userId: String): Result<Int>
    suspend fun migrateOldUserData(userId: String): Result<Unit>
}