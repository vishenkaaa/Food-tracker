package com.example.presentation.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.domain.model.Gender
import com.example.presentation.R

@Composable
fun Gender.displayName(): String{
    return when (this) {
        Gender.MALE -> stringResource(R.string.male)
        Gender.FEMALE -> stringResource(R.string.female)
    }
}

@Composable
fun Gender.imgRes(): Int {
    return when (this) {
        Gender.MALE -> R.drawable.man
        Gender.FEMALE -> R.drawable.woman
    }
}