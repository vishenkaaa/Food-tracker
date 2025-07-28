package com.example.presentation.features.auth.target.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.domain.model.Gender
import com.example.presentation.R
import com.example.presentation.common.ui.components.ContinueButton
import com.example.presentation.extensions.displayName
import com.example.presentation.extensions.imgRes

@Composable
fun GenderSelectionStep(
    selectedGender: Gender?,
    onGenderSelected: (Gender) -> Unit,
    onNextStep: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 88.dp)
    ) {
        Text(
            text = stringResource(R.string.your_gender),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(52.dp)) {
            Gender.entries.forEach { currentGender ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .clickable { onGenderSelected(currentGender) }
                            .shadow(
                                elevation = if (selectedGender == currentGender) 8.dp else 0.dp,
                                shape = RoundedCornerShape(16.dp),
                                clip = false
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedGender == currentGender)
                                MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        ),
                        border = if (selectedGender != currentGender)
                            BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
                        else null,
                    ) {
                        Image(
                            painter = painterResource(currentGender.imgRes()),
                            contentDescription = currentGender.toString(),
                            modifier = Modifier.padding(24.dp),
                            colorFilter = if(selectedGender != currentGender) {
                                ColorFilter.tint(MaterialTheme.colorScheme.outline)
                            } else null
                        )
                    }
                    Text(
                        currentGender.displayName(),
                        color = if (selectedGender == currentGender)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        ContinueButton(selectedGender!= null){
            onNextStep()
        }
    }
}

@Preview
@Composable
fun GenderSelectionStepPreview() {
    GenderSelectionStep(
        Gender.MALE, {}, {})
}