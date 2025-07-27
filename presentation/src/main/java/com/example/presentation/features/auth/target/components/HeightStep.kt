package com.example.presentation.features.auth.target.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.presentation.R

@Composable
fun HeightStep(
    height: Int,
    onHeightSelected: (Int) -> Unit,
    onNextStep: () -> Unit
) {
    NumberInputStep(
        title = stringResource(R.string.your_height),
        value = height.toFloat(),
        unit = stringResource(R.string.centimeters),
        isIntegerInput = true,
        onValueSelected = { value->
            onHeightSelected(value.toInt())
        },
        onNextStep = onNextStep
    )
}