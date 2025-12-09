package com.example.presentation.features.main.profile.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.domain.model.user.Gender
import com.example.domain.model.user.Goal
import com.example.domain.model.user.UserActivityLevel
import com.example.presentation.R
import com.example.presentation.common.ui.values.FoodTrackTheme
import com.example.presentation.extensions.description
import com.example.presentation.extensions.displayName
import com.example.presentation.features.main.profile.models.ProfileEditDialogType
import com.example.presentation.features.main.profile.models.ProfileUiState
import java.time.LocalDate

@Composable
fun ProfileEditDialogs(
    uiState: ProfileUiState,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onGenderUpdate: (Gender) -> Unit,
    onGoalUpdate: (Goal) -> Unit,
    onActivityLevelUpdate: (UserActivityLevel) -> Unit,
    onWeightChangeUpdate: (String) -> Unit,
    onCurrentWeightUpdate: (String) -> Unit,
    onHeightUpdate: (String) -> Unit,
    onBirthDateUpdate: (LocalDate) -> Unit,
    onCaloriesGoalUpdate: (String) -> Unit,
) {
    when (uiState.editDialogType) {
        ProfileEditDialogType.GENDER -> {
            RadioSelectionDialog(
                title = stringResource(R.string.gender),
                items = Gender.entries,
                selectedItem = uiState.tempGender,
                onItemSelected = onGenderUpdate,
                onDismiss = onDismiss,
                onSave = onSave,
                getDisplayName = { it.displayName() }
            )
        }

        ProfileEditDialogType.GOAL -> {
            RadioSelectionDialog(
                title = stringResource(R.string.goal),
                items = Goal.entries,
                selectedItem = uiState.tempGoal,
                onItemSelected = onGoalUpdate,
                onDismiss = onDismiss,
                onSave = onSave,
                getDisplayName = { it.displayName() }
            )
        }

        ProfileEditDialogType.ACTIVITY_LEVEL -> {
            RadioSelectionDialog(
                title = stringResource(R.string.activity_level),
                items = UserActivityLevel.entries,
                selectedItem = uiState.tempActivityLevel,
                onItemSelected = onActivityLevelUpdate,
                onDismiss = onDismiss,
                onSave = onSave,
                getDisplayName = { it.displayName() },
                getDescription = { it.description() }
            )
        }

        ProfileEditDialogType.WEIGHT_CHANGE -> {
            val title = when (uiState.user!!.goal) {
                Goal.LOSE -> stringResource(R.string.how_much_weight_lose)
                Goal.GAIN -> stringResource(R.string.how_much_weight_gain)
                Goal.MAINTAIN -> ""
            }
            val displayValue = if (uiState.user.goal == Goal.LOSE) {
                if (uiState.tempWeightChange.toFloatOrNull() != null && uiState.tempWeightChange.toFloat() < 0) {
                    (-uiState.tempWeightChange.toFloat()).toString()
                } else uiState.tempWeightChange
            } else uiState.tempWeightChange

            NumberInputDialog(
                title = title,
                value = displayValue,
                validation = uiState.validation,
                onValueChanged = onWeightChangeUpdate,
                onDismiss = onDismiss,
                onSave = onSave
            )
        }

        ProfileEditDialogType.CURRENT_WEIGHT -> {
            NumberInputDialog(
                title = stringResource(R.string.current_weight),
                value = uiState.tempCurrentWeight,
                validation = uiState.validation,
                onValueChanged = onCurrentWeightUpdate,
                onDismiss = onDismiss,
                onSave = onSave,
            )
        }

        ProfileEditDialogType.HEIGHT -> {
            NumberInputDialog(
                title = stringResource(R.string.height),
                value = uiState.tempHeight,
                validation = uiState.validation,
                onValueChanged = onHeightUpdate,
                onDismiss = onDismiss,
                onSave = onSave,
                isIntegerInput = true
            )
        }

        ProfileEditDialogType.DATE_OF_BIRTH -> {
            CustomDatePickerDialog(
                selectedDate = uiState.tempBirthDate,
                onDateSelected = onBirthDateUpdate,
                onDismiss = onDismiss,
                onSave = onSave
            )
        }

        ProfileEditDialogType.CALORIES_GOAL -> {
            val recommendedCalories = uiState.user!!.calculateCalories()
            NumberInputDialog(
                title = stringResource(R.string.calories_goal),
                description = recommendedCalories?.let {
                    stringResource(
                        R.string.recommended_daily_calories_intake,
                        it
                    )
                },
                value = uiState.tempCaloriesGoal,
                onValueChanged = onCaloriesGoalUpdate,
                onDismiss = onDismiss,
                onSave = onSave,
                isIntegerInput = true
            )
        }

        null -> {
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileEditDialogsPreview() {
    FoodTrackTheme {
        ProfileEditDialogs(
            uiState = ProfileUiState(),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}
        )
    }
}