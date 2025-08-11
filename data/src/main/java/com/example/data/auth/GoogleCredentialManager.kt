package com.example.data.auth

import android.content.Context
import android.util.Base64
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.data.util.safeCall
import com.example.domain.logger.ErrorLogger
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import java.security.SecureRandom
import javax.inject.Inject

class GoogleCredentialManager @Inject constructor(
    private val credentialManager: CredentialManager,
    private val errorLogger: ErrorLogger
) {
    suspend fun getGoogleIdToken(context: Context, forceNewAccount: Boolean): String = safeCall(errorLogger) {
        val nonce = generateNonce()

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(!forceNewAccount)
            .setServerClientId("86530406637-gk9vs184k3kdi1bhr8gluueelvvs8847.apps.googleusercontent.com")
            .setAutoSelectEnabled(!forceNewAccount)
            .setNonce(nonce)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(context, request)

        val credential = result.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            googleIdTokenCredential.idToken
        } else {
            throw IllegalStateException("Invalid credential type in GoogleCredentialManager.getGoogleIdToken")
        }
    }.getOrThrow()

    private fun generateNonce(): String {
        val random = SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }
}
