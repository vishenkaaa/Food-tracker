package com.example.presentation.features.auth.onboarding.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.domain.model.user.Goal
import com.example.presentation.R
import com.example.presentation.common.ui.values.FoodTrackTheme
import kotlin.math.abs

@Composable
fun WeightChangeStep(
    goal: Goal,
    weightChange: String,
    onWeightChangeSelected: (String) -> Unit,
) {
    val title = when (goal) {
        Goal.LOSE -> stringResource(R.string.how_much_weight_lose)
        Goal.GAIN -> stringResource(R.string.how_much_weight_gain)
        Goal.MAINTAIN -> ""
    }
    NumberInputStep(
        title = title,
        value = abs(weightChange.toFloatOrNull() ?: 0f).toString(),
        unit = stringResource(R.string.kilograms),
        isIntegerInput = false,
        onValueSelected = onWeightChangeSelected,
    )
}

@Composable
@Preview
fun WeightChangeStepPreview() {
    FoodTrackTheme {
        WeightChangeStep(
            Goal.GAIN, ""
        ) {}
    }
}