package com.example.domain.model.auth

sealed class AuthError : Exception() {
    data object NoCredentialAvailable : AuthError() {
        private fun readResolve(): Any = NoCredentialAvailable
    }

    data object InvalidCredentialType : AuthError() {
        private fun readResolve(): Any = InvalidCredentialType
    }

    data object NetworkError : AuthError() {
        private fun readResolve(): Any = NetworkError
    }

    data object UserCancelled : AuthError() {
        private fun readResolve(): Any = UserCancelled
    }

    data class UnknownError(val originalException: Exception) : AuthError()

    data object ReauthenticationRequired : AuthError() {
        private fun readResolve(): Any = ReauthenticationRequired
    }
}