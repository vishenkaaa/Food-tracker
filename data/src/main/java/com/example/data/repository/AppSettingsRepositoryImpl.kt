package com.example.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.util.safeCall
import com.example.domain.logger.ErrorLogger
import com.example.domain.repository.AppSettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

@Singleton
class AppSettingsRepositoryImpl @Inject constructor(
    private val context: Context,
    private val errorLogger: ErrorLogger
) : AppSettingsRepository {

    override suspend fun getBoolean(key: String): Result<Boolean> = safeCall(errorLogger) {
        val preferencesKey = booleanPreferencesKey(key)

        context.dataStore.data
            .first()[preferencesKey] ?: false
    }

    override suspend fun saveBoolean(key: String, value: Boolean) {
        context.dataStore.edit { settings ->
            val preferencesKey = booleanPreferencesKey(key)
            settings[preferencesKey] = value
        }
    }
}