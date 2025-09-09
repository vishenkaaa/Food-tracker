package com.example.presentation.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.domain.model.statistics.StatisticsPeriod
import com.example.presentation.R

@Composable
fun StatisticsPeriod.displayName(): String{
    return when (this){
        StatisticsPeriod.TODAY -> stringResource(R.string.today)
        StatisticsPeriod.YESTERDAY -> stringResource(R.string.yesterday)
        StatisticsPeriod.WEEK -> stringResource(R.string.week)
    }
}