package com.example.data.di

import com.example.data.repository.UserRepositoryImpl
import com.example.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UserRepositoryModule {

    @Provides
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    fun provideUserRepository(
        firestore: FirebaseFirestore
    ): UserRepository = UserRepositoryImpl(firestore)
}