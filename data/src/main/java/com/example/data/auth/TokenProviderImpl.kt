package com.example.data.auth

import android.content.Context
import com.example.data.logger.CrashlyticsErrorLogger
import com.example.data.util.safeCall
import com.example.domain.repository.TokenProvider
import javax.inject.Inject

class TokenProviderImpl @Inject constructor(
    private val context: Context,
    private val errorLogger: CrashlyticsErrorLogger,
    private val googleCredentialManager: GoogleCredentialManager
) : TokenProvider {

    override suspend fun getGoogleIdToken(forceNewAccount: Boolean): Result<String> = safeCall(errorLogger) {
        googleCredentialManager.getGoogleIdToken(
            context = context,
            forceNewAccount = forceNewAccount
        )
    }
}