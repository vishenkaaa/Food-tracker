package com.example.presentation.features.auth.target.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.domain.model.UserActivityLevel
import com.example.presentation.R
import com.example.presentation.common.ui.components.ContinueButton
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun BirthDateStep(
    selectedBirthDate: LocalDate?,
    onBirthDateSelected: (LocalDate) -> Unit,
    onNextStep: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedBirthDate?.toEpochDay()?.let {
            LocalDate.ofEpochDay(it).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 58.dp)
    ) {
        Text(
            text = stringResource(R.string.your_birthdate),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            title = null,
        )

        Spacer(modifier = Modifier.weight(1f))

        ContinueButton(datePickerState.selectedDateMillis != null) {
            val selectedMillis = datePickerState.selectedDateMillis
            if (selectedMillis != null) {
                val localDate = Instant.ofEpochMilli(selectedMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                onBirthDateSelected(localDate)
                onNextStep()
            }
        }
    }
}

@Preview
@Composable
fun BirthDateStep() {
    UserActivityLevelSectionStep(
        UserActivityLevel.ACTIVE, {}, {})
}