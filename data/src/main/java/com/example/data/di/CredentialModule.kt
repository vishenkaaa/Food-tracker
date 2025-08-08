package com.example.data.di

import android.content.Context
import androidx.credentials.CredentialManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CredentialModule {

    @Provides
    @Singleton
    fun provideCredentialManager(context: Context): CredentialManager {
        return CredentialManager.create(context)
    }
}
