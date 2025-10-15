package com.example.data.di

import com.example.data.repository.AppSettingsRepositoryImpl
import com.example.data.repository.CameraRepositoryImpl
import com.example.data.repository.GptRepositoryImpl
import com.example.data.repository.MealRepositoryImpl
import com.example.data.repository.UserRepositoryImpl
import com.example.domain.repository.AppSettingsRepository
import com.example.domain.repository.CameraRepository
import com.example.domain.repository.GptRepository
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

    @Binds
    @Singleton
    abstract fun bindGptRepository(
        gptRepositoryImpl: GptRepositoryImpl
    ): GptRepository

    @Binds
    @Singleton
    abstract fun bindAppSettingsRepository(
        appSettingsRepositoryImpl: AppSettingsRepositoryImpl
    ): AppSettingsRepository
}