package com.example.domain.logger

interface ErrorLogger {
    fun logException(e: Throwable)
}