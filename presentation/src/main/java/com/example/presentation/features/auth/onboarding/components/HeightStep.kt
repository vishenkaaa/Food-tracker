package com.example.presentation.features.auth.onboarding.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.presentation.R

@Composable
fun HeightStep(
    height: String,
    onHeightSelected: (String) -> Unit,
    onNextStep: () -> Unit
) {
    NumberInputStep(
        title = stringResource(R.string.your_height),
        value = height,
        unit = stringResource(R.string.centimeters),
        isIntegerInput = true,
        onValueSelected = onHeightSelected
    )
}