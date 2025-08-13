package com.example.domain.repository

interface TokenProvider {
    suspend fun getGoogleIdToken(forceNewAccount: Boolean): Result<String>
}