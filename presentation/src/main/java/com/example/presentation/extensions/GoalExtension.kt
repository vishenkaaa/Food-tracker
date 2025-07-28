package com.example.presentation.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.domain.model.Goal
import com.example.presentation.R

@Composable
fun Goal.displayName(): String{
    return when (this) {
        Goal.LOSE -> stringResource(R.string.weight_loss)
        Goal.MAINTAIN -> stringResource(R.string.maintaining_current_weight)
        Goal.GAIN -> stringResource(R.string.weight_gain)
    }
}