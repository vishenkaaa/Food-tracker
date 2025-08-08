package com.example.data.di

import com.example.data.repository.FirebaseAuthRepositoryImpl
import com.example.domain.repository.FirebaseAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFirebaseAuthRepository(
        impl: FirebaseAuthRepositoryImpl
    ): FirebaseAuthRepository
}