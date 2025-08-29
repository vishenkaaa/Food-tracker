package com.example.domain.model.auth

sealed class AuthError() : Exception() {
    data object NoCredentialAvailable : AuthError()
    data object InvalidCredentialType : AuthError()
    data object NetworkError : AuthError()
    data object UserCancelled : AuthError()
    data class UnknownError(val originalException: Exception) : AuthError()
}