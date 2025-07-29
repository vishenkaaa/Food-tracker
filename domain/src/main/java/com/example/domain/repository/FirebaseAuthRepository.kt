package com.example.domain.repository

import com.example.domain.model.User

interface FirebaseAuthRepository {
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signOut()

    suspend fun createUser(user: User): Result<Unit>
    suspend fun isUserLoggedIn(): Boolean
    suspend fun getCurrentUserId(): String?
    suspend fun isUserFullyRegistered(userId: String): Boolean
}
