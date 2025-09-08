package com.example.presentation.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.presentation.R
import com.example.presentation.features.main.diary.addMeals.models.AddMealTab

@Composable
fun AddMealTab.displayName(): String{
    return when(this){
        AddMealTab.AI -> "AI"
        AddMealTab.BARCODE -> stringResource(R.string.barcode)
    }
}

@Composable
fun AddMealTab.icon(): Painter {
    return when(this){
        AddMealTab.AI -> painterResource(R.drawable.head_circuit)
        AddMealTab.BARCODE -> painterResource(R.drawable.barcode)
    }
}