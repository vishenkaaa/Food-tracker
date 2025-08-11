package com.example.presentation.features.auth.onboarding.models

import androidx.compose.runtime.saveable.Saver
import com.example.domain.model.user.Gender
import com.example.domain.model.user.Goal
import com.example.domain.model.user.UserActivityLevel
import java.time.LocalDate

val GoalSaver = Saver<Goal?, String>(
    save = { it?.value },
    restore = { savedValue -> savedValue.let { Goal.fromValue(it) } }
)

val GenderSaver = Saver<Gender?, String>(
    save = { it?.value },
    restore = { savedValue -> savedValue.let { Gender.fromValue(it) } }
)

val UserActivityLevelSaver = Saver<UserActivityLevel?, String>(
    save = { it?.value },
    restore = { savedValue -> savedValue.let { UserActivityLevel.fromValue(it) } }
)

val LocalDateSaver = Saver<LocalDate?, String>(
    save = { it?.toString() },
    restore = { savedValue -> savedValue.let { LocalDate.parse(it) } }
)