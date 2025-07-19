package com.example.domain.repository

import com.example.domain.model.User

interface FirebaseAuthRepository {
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signOut()
}
