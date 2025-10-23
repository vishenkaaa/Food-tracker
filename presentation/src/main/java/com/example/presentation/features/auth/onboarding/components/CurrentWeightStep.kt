package com.example.presentation.features.auth.onboarding.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.presentation.R
import com.example.presentation.common.ui.values.FoodTrackTheme
import com.example.presentation.features.auth.onboarding.models.InputValidation

@Composable
fun CurrentWeightStep(
    currentWeight: String,
    validation: (InputValidation) = InputValidation(),
    onCurrentWeightSelected: (String) -> Unit,
    onNextStep: () -> Unit
) {
    NumberInputStep(
        title = stringResource(R.string.your_current_weight),
        value = currentWeight,
        unit = stringResource(R.string.kilograms),
        isIntegerInput = false,
        validation = validation,
        onValueSelected = onCurrentWeightSelected,
        onNextStep = onNextStep
    )
}

@Preview(showBackground = true)
@Composable
fun CurrentWeightStepPreview() {
    FoodTrackTheme {
        CurrentWeightStep(
            currentWeight = "",
            onNextStep = {},
            onCurrentWeightSelected = {}
        )
    }
}