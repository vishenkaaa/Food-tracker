package com.example.data.util

import com.example.domain.logger.ErrorLogger

suspend inline fun <T> safeCall(
    errorLogger: ErrorLogger,
    block: () -> T
): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Exception) {
        errorLogger.logException(e)
        Result.failure(e)
    }
}
