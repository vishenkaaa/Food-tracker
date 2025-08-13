package com.example.domain.usecase.auth

import com.example.domain.repository.TokenProvider
import javax.inject.Inject

class GetGoogleIdTokenUseCase @Inject constructor(
    private val tokenProvider: TokenProvider
) {
    suspend operator fun invoke(forceNewAccount: Boolean): String{
        return tokenProvider.getGoogleIdToken(forceNewAccount).getOrThrow()
    }
}