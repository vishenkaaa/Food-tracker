package com.example.presentation.features.auth.onboarding.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.presentation.R

@Composable
fun CurrentWeightStep(
    currentWeight: String,
    onCurrentWeightSelected: (String) -> Unit,
) {
    NumberInputStep(
        title = stringResource(R.string.your_current_weight),
        value = currentWeight,
        unit = stringResource(R.string.kilograms),
        isIntegerInput = false,
        onValueSelected = onCurrentWeightSelected,
    )
}