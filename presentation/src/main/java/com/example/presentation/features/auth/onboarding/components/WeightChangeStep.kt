package com.example.presentation.features.auth.onboarding.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.domain.model.user.Goal
import com.example.presentation.R
import com.example.presentation.common.ui.values.FoodTrackTheme
import com.example.presentation.features.auth.onboarding.models.InputValidation

@Composable
fun WeightChangeStep(
    goal: Goal,
    weightChange: String,
    validation: (InputValidation) = InputValidation(),
    onWeightChangeSelected: (String) -> Unit,
    onNextStep: () -> Unit
) {
    val title = when (goal) {
        Goal.LOSE -> stringResource(R.string.how_much_weight_lose)
        Goal.GAIN -> stringResource(R.string.how_much_weight_gain)
        Goal.MAINTAIN -> ""
    }
    NumberInputStep(
        title = title,
        value = weightChange,
        unit = stringResource(R.string.kilograms),
        isIntegerInput = false,
        validation = validation,
        onValueSelected = onWeightChangeSelected,
        onNextStep = onNextStep
    )
}

@Composable
@Preview
fun WeightChangeStepPreview() {
    FoodTrackTheme {
        WeightChangeStep(
            goal = Goal.GAIN,
            weightChange = "",
            onNextStep = {},
            onWeightChangeSelected = {}
        )
    }
}