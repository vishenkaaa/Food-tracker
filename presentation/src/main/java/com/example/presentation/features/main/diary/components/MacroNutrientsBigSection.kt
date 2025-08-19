package com.example.presentation.features.main.diary.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.presentation.R

@Composable
fun MacroNutrientsBigSection(protein: Int, fat: Int, carbs: Int){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        MacroNutrientBigItem(
            label = stringResource(R.string.protein),
            value = stringResource(R.string.g, protein)
        )

        MacroNutrientBigItem(
            label = stringResource(R.string.carb),
            value = stringResource(R.string.g, carbs)
        )

        MacroNutrientBigItem(
            label = stringResource(R.string.fat),
            value = stringResource(R.string.g, fat)
        )
    }
}