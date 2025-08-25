package com.example.presentation.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.domain.model.diary.MealType
import com.example.domain.model.diary.UnitType
import com.example.presentation.R

@Composable
fun UnitType.displayName(): String {
    return when(this){
        UnitType.GRAM -> stringResource(R.string.unit_gram)
        UnitType.MILLILITER -> stringResource(R.string.unit_milliliter)
        UnitType.LITER -> stringResource(R.string.unit_liter)
        UnitType.PIECE -> stringResource(R.string.unit_piece)
        UnitType.KILOGRAM -> stringResource(R.string.unit_kilogram)
    }
}