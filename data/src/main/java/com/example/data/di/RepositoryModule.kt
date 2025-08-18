package com.example.data.di

import com.example.data.repository.CameraRepositoryImpl
import com.example.data.repository.MealRepositoryImpl
import com.example.data.repository.UserRepositoryImpl
import com.example.domain.repository.CameraRepository
import com.example.domain.repository.MealRepository
import com.example.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindMealRepository(
        mealRepositoryImpl: MealRepositoryImpl
    ): MealRepository

    @Binds
    @Singleton
    abstract fun bindCameraRepository(
        cameraRepositoryImpl: CameraRepositoryImpl
    ): CameraRepository
}