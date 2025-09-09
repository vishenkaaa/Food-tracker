package com.example.data.auth

import com.example.domain.repository.TokenProvider
import javax.inject.Inject

class TokenProviderImpl @Inject constructor(
    private val googleCredentialManager: GoogleCredentialManager,
) : TokenProvider {

    override suspend fun getGoogleIdToken(forceNewAccount: Boolean): Result<String> {
        return googleCredentialManager.getGoogleIdToken(forceNewAccount)
    }
}