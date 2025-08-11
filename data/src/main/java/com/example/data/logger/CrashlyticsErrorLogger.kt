package com.example.data.logger

import com.example.domain.logger.ErrorLogger
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

class CrashlyticsErrorLogger @Inject constructor(): ErrorLogger {
    override fun logException(e: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(e)
    }
}