package com.example.domain.repository

interface AppSettingsRepository {
    suspend fun getBoolean(key: String): Result<Boolean>
    suspend fun saveBoolean(key: String, value: Boolean)
}