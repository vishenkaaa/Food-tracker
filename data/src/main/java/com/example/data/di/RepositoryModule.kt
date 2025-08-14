package com.example.data.di

import com.example.data.repository.MealRepositoryImpl
import com.example.data.repository.UserRepositoryImpl
import com.example.domain.logger.ErrorLogger
import com.example.domain.repository.MealRepository
import com.example.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        errorLogger: ErrorLogger
    ): UserRepository = UserRepositoryImpl(firestore, errorLogger)

    @Provides
    @Singleton
    fun provideMealRepository(
        firestore: FirebaseFirestore,
        errorLogger: ErrorLogger
    ): MealRepository = MealRepositoryImpl(firestore, errorLogger)
}