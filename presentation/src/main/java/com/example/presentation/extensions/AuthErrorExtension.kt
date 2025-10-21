package com.example.presentation.extensions

import android.content.Context
import com.example.domain.model.auth.AuthError
import com.example.presentation.R

fun AuthError.getLocalizedMessage(context: Context): String {
    return when (this) {
        is AuthError.NoCredentialAvailable -> context.getString(R.string.error_no_credential_available)
        is AuthError.InvalidCredentialType -> context.getString(R.string.error_invalid_credential_type)
        is AuthError.NetworkError -> context.getString(R.string.no_internet_connection)
        is AuthError.UserCancelled -> context.getString(R.string.error_user_cancelled_auth)
        is AuthError.UnknownError -> context.getString(R.string.error_unknown_auth)
        is AuthError.ReauthenticationRequired -> context.getString(R.string.error_account_deletion)
    }
}