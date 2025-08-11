package com.example.presentation.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.domain.model.user.UserActivityLevel
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
        UserActivityLevel.SEDENTARY -> stringResource(R.string.sedentary_user_activities_description)
        UserActivityLevel.LIGHTLY_ACTIVE -> stringResource(R.string.lightly_active_user_activities_description)
        UserActivityLevel.ACTIVE -> stringResource(R.string.active_user_activities_description)
        UserActivityLevel.VERY_ACTIVE -> stringResource(R.string.very_active_user_activities_description)
    }
}
