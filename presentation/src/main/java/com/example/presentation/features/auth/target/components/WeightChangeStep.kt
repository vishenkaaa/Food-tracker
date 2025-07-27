package com.example.presentation.features.auth.target.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.domain.model.Goal
import com.example.presentation.R

@Composable
fun WeightChangeStep(
    goal: Goal,
    weightChange: Float,
    onWeightChangeSelected: (Float) -> Unit,
    onNextStep: () -> Unit
) {
    val title = when(goal){
        Goal.LOSE -> stringResource(R.string.how_much_weight_lose)
        Goal.GAIN -> stringResource(R.string.how_much_weight_gain)
        Goal.MAINTAIN -> ""
    }
    NumberInputStep(
        title = title,
        value = if (goal == Goal.LOSE) -weightChange else weightChange,
        unit = stringResource(R.string.kilograms),
        isIntegerInput = false,
        onValueSelected = { value ->
            val adjustedValue = if (goal == Goal.LOSE) -value else value
            onWeightChangeSelected(adjustedValue)
        },
        onNextStep = onNextStep
    )
}