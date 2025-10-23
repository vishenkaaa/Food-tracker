package com.example.presentation.features.auth.onboarding.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.presentation.common.ui.values.FoodTrackTheme
import com.example.presentation.features.auth.onboarding.models.InputValidation

@Composable
fun NumberInputStep(
    title: String,
    value: String,
    unit: String,
    isIntegerInput: Boolean = false,
    validation: (InputValidation) = InputValidation(),
    onValueSelected: (String) -> Unit,
    onNextStep: () -> Unit
) {
    var input by remember {
        mutableStateOf(
            TextFieldValue(
                text = "",
                selection = TextRange(0)
            )
        )
    }

    LaunchedEffect(value) {
        if (value != input.text) {
            val initialValue = if (value.isNotEmpty() && value.toFloatOrNull() != null && value.toFloat() > 0) {
                if (isIntegerInput) value.toFloat().toInt().toString()
                else value
            } else ""

            input = TextFieldValue(
                text = initialValue,
                selection = TextRange(initialValue.length)
            )
        }
    }

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var hasFocusBeenRequested by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 88.dp)
            .clickable(
                indication = null, interactionSource = remember { MutableInteractionSource() }) {
                focusManager.clearFocus()
            }
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(IntrinsicSize.Min)
        ) {
            OutlinedTextField(
                value = input,

                onValueChange = { newValue ->
                    val text = newValue.text
                    val isDecimal = !isIntegerInput
                    val maxDecimalPlaces = 1

                    val filteredText = if (isDecimal) {
                        text.filterIndexed { _, char ->
                            char.isDigit() || (char == '.' && text.count { it == '.' } <= 1)
                        }
                    } else {
                        text.filter { it.isDigit() }
                    }

                    var finalValue = filteredText

                    if (finalValue.length > 1 && finalValue.startsWith('0') && finalValue[1] != '.')
                        finalValue = finalValue.trimStart('0')

                    if (isDecimal) {
                        if (finalValue == ".")
                            finalValue = "0."

                        val parts = finalValue.split(".")
                        if (parts.size > 1 && parts[1].length > maxDecimalPlaces)
                            finalValue = "${parts[0]}.${parts[1].substring(0, maxDecimalPlaces)}"
                    }

                    input = newValue.copy(
                        text = finalValue,
                        selection = TextRange(finalValue.length)
                    )
                    onValueSelected(finalValue)
                },
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
                label = null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (isIntegerInput) KeyboardType.Number else KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        onNextStep()
                    }
                ),
                singleLine = true,
                modifier = Modifier
                    .width(140.dp)
                    .focusRequester(focusRequester)
                    .onGloballyPositioned {
                        if (!hasFocusBeenRequested) {
                            focusRequester.requestFocus()
                            hasFocusBeenRequested = true
                        }
                    },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                ),
                shape = RoundedCornerShape(16.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)
                    )
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    unit,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        AnimatedVisibility(
            visible = !validation.isValid && validation.errorMessage != null,
//            enter = fadeIn() + expandVertically(),
//            exit = fadeOut() + shrinkVertically(),

            enter = slideInVertically(
                initialOffsetY = { it }
            ) + fadeIn(animationSpec = tween(durationMillis = 200)),

            exit = slideOutVertically(
                targetOffsetY = { it }
            ) + fadeOut(animationSpec = tween(durationMillis = 200))
        ) {
            Text(
                text = validation.errorMessage ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NumberInputStepPreview() {
    FoodTrackTheme {
        NumberInputStep(
            "Ваша ціль набору ваги",
            "0",
            "Кг",
            false,
            onNextStep = {},
            onValueSelected = {}
        )
    }
}