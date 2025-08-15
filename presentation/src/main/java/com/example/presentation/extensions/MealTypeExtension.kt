package com.example.presentation.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.domain.model.diary.MealType
import com.example.presentation.R

@Composable
fun MealType.displayName(): String{
    return when(this){
        MealType.BREAKFAST -> stringResource(R.string.breakfast)
        MealType.LUNCH -> stringResource(R.string.lunch)
        MealType.DINNER -> stringResource(R.string.dinner)
        MealType.SNACKS -> stringResource(R.string.snack)
    }
}

@Composable
fun MealType.icon(): Painter{
    return when(this){
        MealType.BREAKFAST -> painterResource(R.drawable.sundim)
        MealType.LUNCH -> painterResource(R.drawable.lunch)
        MealType.DINNER -> painterResource(R.drawable.dinner)
        MealType.SNACKS -> painterResource(R.drawable.snack)
    }
}