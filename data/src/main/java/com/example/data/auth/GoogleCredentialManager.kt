package com.example.data.auth

import android.content.Context
import android.util.Base64
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.GetCredentialUnsupportedException
import androidx.credentials.exceptions.NoCredentialException
import com.example.common.ActivityHolder
import com.example.data.R
import com.example.domain.logger.ErrorLogger
import com.example.domain.model.auth.AuthError
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.SecureRandom
import javax.inject.Inject

class GoogleCredentialManager @Inject constructor(
    private val credentialManager: CredentialManager,
    private val errorLogger: ErrorLogger,
    private val activityHolder: ActivityHolder,
    @ApplicationContext private val context: Context
) {
    suspend fun getGoogleIdToken(forceNewAccount: Boolean): Result<String> {
        return try {
            val activity = activityHolder.getActivity()
                ?: return Result.failure(AuthError.UnknownError(Exception("Activity context not available")))

            val nonce = generateNonce()

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(!forceNewAccount)
                .setServerClientId(context.getString(R.string.firebase_client_id))
                .setAutoSelectEnabled(!forceNewAccount)
                .setNonce(nonce)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(activity, request)

            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                Result.success(googleIdTokenCredential.idToken)
            } else {
                val error = AuthError.InvalidCredentialType
                errorLogger.logException(error)
                Result.failure(error)
            }
        } catch (e: Exception) {
            val authError = when (e) {
                is NoCredentialException -> AuthError.NoCredentialAvailable
                is GetCredentialCancellationException -> AuthError.UserCancelled
                is GetCredentialInterruptedException -> AuthError.UserCancelled
                is GetCredentialProviderConfigurationException -> AuthError.NetworkError
                is GetCredentialUnsupportedException -> AuthError.NoCredentialAvailable
                else -> AuthError.UnknownError(e)
            }
            errorLogger.logException(authError)
            Result.failure(authError)
        }
    }

    private fun generateNonce(): String {
        val random = SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }
}
