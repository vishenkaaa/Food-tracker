package com.example.presentation.features.auth.onboarding.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.presentation.R
import com.example.presentation.common.ui.values.FoodTrackTheme
import com.example.presentation.features.auth.onboarding.models.InputValidation

@Composable
fun HeightStep(
    height: String,
    validation: (InputValidation) = InputValidation(),
    onHeightSelected: (String) -> Unit,
    onNextStep: () -> Unit
) {
    NumberInputStep(
        title = stringResource(R.string.your_height),
        value = height,
        unit = stringResource(R.string.centimeters),
        isIntegerInput = true,
        validation = validation,
        onValueSelected = onHeightSelected,
        onNextStep = onNextStep
    )
}

@Preview(showBackground = true)
@Composable
fun HeightStepPreview() {
    FoodTrackTheme {
        HeightStep(
            height = "", 
            onNextStep = {},
            onHeightSelected = {}
        )
    }
}