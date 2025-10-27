package com.example.presentation.features.auth.onboarding.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.presentation.R
import com.example.presentation.common.ui.values.FoodTrackTheme
import com.example.presentation.features.auth.onboarding.models.InputValidation

@Composable
fun NameStep(
    name: String,
    validation: (InputValidation) = InputValidation(),
    onNameSelected: (String) -> Unit,
    onNextStep: () -> Unit
) {
    val maxLength = 25

    var input by remember(name) {
        mutableStateOf(
            TextFieldValue(
                text = name,
                selection = TextRange(name.length)
            )
        )
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
            text = stringResource(R.string.what_s_your_name),
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

                    val finalValue = if (text.length > maxLength) {
                        text.substring(0, maxLength)
                    } else {
                        text
                    }

                    input = newValue.copy(
                        text = finalValue,
                        selection = TextRange(finalValue.length)
                    )

                    onNameSelected(finalValue)
                },
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
                label = null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
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
                    .width(280.dp)
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
        }

        AnimatedVisibility(
            visible = !validation.isValid && validation.errorMessage != null,

            enter = slideInVertically(
                initialOffsetY = { -it }
            ) + fadeIn(animationSpec = tween(durationMillis = 200)),

            exit = slideOutVertically(
                targetOffsetY = { -it }
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
fun NameStepPreview() {
    FoodTrackTheme {
        NameStep (
            "Аня",
            onNextStep = {},
            onNameSelected = {}
        )
    }
}