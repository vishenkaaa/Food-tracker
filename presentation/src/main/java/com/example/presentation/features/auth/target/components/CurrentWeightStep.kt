package com.example.presentation.features.auth.target.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.presentation.R

@Composable
fun CurrentWeightStep(
    currentWeight: Float,
    onCurrentWeightSelected: (Float) -> Unit,
    onNextStep: () -> Unit
) {
    NumberInputStep(
        title = stringResource(R.string.your_current_weight),
        value = currentWeight,
        unit = stringResource(R.string.kilograms),
        isIntegerInput = false,
        onValueSelected = onCurrentWeightSelected,
        onNextStep = onNextStep
    )
}