package com.example.data.util

import com.example.domain.logger.ErrorLogger
import com.example.domain.model.auth.AuthError
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException

suspend inline fun <T> safeCall(
    errorLogger: ErrorLogger,
    block: () -> T
): Result<T> {
    return try {
        Result.success(block())
    }
    catch (e: FirebaseAuthRecentLoginRequiredException) {
        return Result.failure(AuthError.ReauthenticationRequired)
    }
    catch (e: Exception) {
        errorLogger.logException(e)
        Result.failure(e)
    }
}
