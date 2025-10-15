package com.example.domain.usecase.user

import com.example.domain.repository.AppSettingsRepository
import com.example.domain.repository.UserRepository
import javax.inject.Inject

class MigrateUserDataUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val appSettingsRepository: AppSettingsRepository
) {
    companion object {
        private const val MIGRATION_V1_KEY = "migration_user_weight_v1_done"
    }

    suspend operator fun invoke(userId: String): Result<Unit> {
        val isMigrationDone =
            appSettingsRepository.getBoolean(MIGRATION_V1_KEY).getOrNull() ?: false

        if (isMigrationDone)
            return Result.success(Unit)

        val result = userRepository.migrateOldUserData(userId)

        return if (result.isSuccess) {
            appSettingsRepository.saveBoolean(MIGRATION_V1_KEY, true)
            Result.success(Unit)
        } else
            result

    }
}