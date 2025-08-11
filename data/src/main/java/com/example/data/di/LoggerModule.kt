package com.example.data.di

import com.example.data.logger.CrashlyticsErrorLogger
import com.example.domain.logger.ErrorLogger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LoggerModule {

    @Binds
    abstract fun bindErrorLogger(
        impl: CrashlyticsErrorLogger
    ): ErrorLogger
}