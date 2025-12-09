package com.example.presentation.features.main.profile.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.presentation.R
import com.example.presentation.common.ui.values.FoodTrackTheme
import com.example.presentation.features.auth.onboarding.models.InputValidation

@Composable
fun NumberInputDialog(
    title: String,
    description: String? = null,
    isIntegerInput: Boolean = false,
    validation: InputValidation = InputValidation(),
    value: String,
    onValueChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var input by remember(value) {
        val initialValue =
            if (value.isNotEmpty() && value.toFloatOrNull() != null && value.toFloat() > 0) {
                if (isIntegerInput) value.toFloat().toInt().toString()
                else value
            } else ""

        mutableStateOf(
            TextFieldValue(
                text = initialValue,
                selection = TextRange(initialValue.length)
            )
        )
    }

    val focusRequester = remember { FocusRequester() }
    var hasFocusBeenRequested by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                description?.let { desc ->
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        },
        text = {
            Column {
                BasicTextField(
                    value = input,
                    onValueChange = { newValue ->
                        val filteredText = if (isIntegerInput) newValue.text.filter { it.isDigit() }
                        else {
                            val text = newValue.text
                            if (text.count { it == '.' } <= 1 && text.all { it.isDigit() || it == '.' })
                                text
                            else input.text
                        }

                        onValueChanged(filteredText)
                    },

                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (isIntegerInput) KeyboardType.Number else KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    textStyle = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onGloballyPositioned {
                            if (!hasFocusBeenRequested) {
                                focusRequester.requestFocus()
                                hasFocusBeenRequested = true
                            }
                        },
                ) { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = input.text,
                        innerTextField = innerTextField,
                        enabled = true,
                        singleLine = true,
                        visualTransformation = VisualTransformation.None,
                        interactionSource = remember { MutableInteractionSource() },
                        contentPadding = PaddingValues(top = 4.dp, bottom = 0.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            errorIndicatorColor = MaterialTheme.colorScheme.error
                        )
                    )
                }

                if (!validation.isValid && validation.errorMessage != null) {
                    Text(
                        text = validation.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
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
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.background
    )
}

@Preview(showBackground = true)
@Composable
fun NumberInputDialogPreview() {
    FoodTrackTheme {
        NumberInputDialog(
            title = "Вага",
            value = "",
            onDismiss = {},
            onSave = {},
            onValueChanged = {}
        )
    }
}