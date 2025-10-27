package com.example.domain.repository

import com.example.domain.model.user.User

interface FirebaseAuthRepository {
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signOut()
    suspend fun deleteAccount(): Result<Unit>
    suspend fun isUserLoggedIn(): Boolean
    suspend fun getCurrentUserId(): String?
    suspend fun getCurrentUserEmail(): String?
}
