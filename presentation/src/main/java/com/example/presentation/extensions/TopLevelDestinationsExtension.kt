package com.example.presentation.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.presentation.R
import com.example.presentation.features.main.navigation.TopLevelDestinations

@Composable
fun TopLevelDestinations.toTitle(): String{
    return when (this){
        TopLevelDestinations.DIARY -> stringResource(R.string.diary)
        TopLevelDestinations.STATISTICS -> stringResource(R.string.statistics)
        TopLevelDestinations.PROFILE -> stringResource(R.string.profile)
    }
}

@Composable
fun TopLevelDestinations.toIcon(): Painter{
    return when (this){
        TopLevelDestinations.DIARY -> painterResource(R.drawable.nav_diary)
        TopLevelDestinations.STATISTICS -> painterResource(R.drawable.nav_statistics)
        TopLevelDestinations.PROFILE -> painterResource(R.drawable.nav_profile)
    }
}