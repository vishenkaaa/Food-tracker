package com.example.data.di

import com.example.data.auth.TokenProviderImpl
import com.example.data.repository.FirebaseAuthRepositoryImpl
import com.example.domain.repository.FirebaseAuthRepository
import com.example.domain.repository.TokenProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindFirebaseAuthRepository(
        impl: FirebaseAuthRepositoryImpl
    ): FirebaseAuthRepository

    @Binds
    abstract fun bindTokenProvider(
        tokenProviderImpl: TokenProviderImpl
    ): TokenProvider
}