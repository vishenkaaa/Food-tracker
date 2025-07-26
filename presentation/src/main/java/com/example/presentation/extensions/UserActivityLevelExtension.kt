package com.example.presentation.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.domain.model.UserActivityLevel
import com.example.presentation.R

@Composable
fun UserActivityLevel.displayName(): String {
    return when (this) {
        UserActivityLevel.SEDENTARY -> stringResource(R.string.sedentary)
        UserActivityLevel.LIGHTLY_ACTIVE -> stringResource(R.string.lightly_active)
        UserActivityLevel.ACTIVE -> stringResource(R.string.active)
        UserActivityLevel.VERY_ACTIVE -> stringResource(R.string.very_active)
    }
}

@Composable
fun UserActivityLevel.description(): String {
    return when (this) {
        UserActivityLevel.SEDENTARY -> stringResource(R.string.daily_activities_that_require_minimal_effort)
        UserActivityLevel.LIGHTLY_ACTIVE -> stringResource(R.string.daily_activities_that_require_light_effort)
        UserActivityLevel.ACTIVE -> stringResource(R.string.daily_activities_that_require_moderate_effort)
        UserActivityLevel.VERY_ACTIVE -> stringResource(R.string.daily_activities_that_require_intense_physical_effort)
    }
}
