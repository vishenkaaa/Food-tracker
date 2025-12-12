package com.example.presentation.features.main.profile.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.presentation.R
import com.example.presentation.common.ui.values.DatePickerTypography
import com.example.presentation.common.ui.values.FoodTrackTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun CustomDatePickerDialog(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate?.toEpochDay()?.let {
            LocalDate.ofEpochDay(it).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        },
        yearRange = IntRange(1900, LocalDate.now().year),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val dateToCheck = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                val threeYearsAgo = LocalDate.now().minusYears(3)
                return dateToCheck <= threeYearsAgo
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year <= LocalDate.now().year
            }
        }
    )

    LaunchedEffect(datePickerState.selectedDateMillis) {
        val millis = datePickerState.selectedDateMillis
        if (millis != null) {
            val localDate =
                Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
            onDateSelected(localDate)
        }
    }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onSave, shape = RoundedCornerShape(16.dp)) {
                Text(
                    text = stringResource(R.string.save),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        dismissButton = {
            TextButton(onDismiss, shape = RoundedCornerShape(16.dp)) {
                Text(
                    text = stringResource(R.string.cancel),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        MaterialTheme(
            typography = DatePickerTypography
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.background,
                    headlineContentColor = MaterialTheme.colorScheme.onBackground, // Колір вибраної дати
                    weekdayContentColor = MaterialTheme.colorScheme.onBackground, // Колір днів тижня
                    dayContentColor = MaterialTheme.colorScheme.onBackground, // Колір днів
                    navigationContentColor = MaterialTheme.colorScheme.onBackground, // Колір місяця
                    yearContentColor = MaterialTheme.colorScheme.onBackground, // Колір року
                    todayContentColor = MaterialTheme.colorScheme.onBackground, // Колір сьогоднішнього дня
                    todayDateBorderColor = MaterialTheme.colorScheme.background, // Колір рамки сьогоднішнього дня
                ),
                title = {
                    Text(
                        text = stringResource(R.string.date_of_birth),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 24.dp, start = 24.dp)
                    )
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomDatePickerDialogPreview() {
    FoodTrackTheme {
        CustomDatePickerDialog(
            LocalDate.now(), {}, {}
        ) {}
    }
}