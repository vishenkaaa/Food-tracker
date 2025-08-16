package com.example.presentation.features.main.diary.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.presentation.R

@Composable
fun MacroNutrientsSmallSection(protein: Int, fat: Int, carbs: Int){
    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        MacroNutrientSmallItem(
            stringResource(R.string.protein),
            protein
        )
        MacroNutrientSmallItem(
            stringResource(R.string.fat),
            fat
        )
        MacroNutrientSmallItem(
            stringResource(R.string.carb),
            carbs
        )
    }
}