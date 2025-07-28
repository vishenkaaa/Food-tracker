package com.example.presentation.features.auth.target.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.domain.model.Goal
import com.example.domain.model.UserActivityLevel
import com.example.presentation.R
import com.example.presentation.common.ui.components.ContinueButton
import com.example.presentation.extensions.description
import com.example.presentation.extensions.displayName

@Composable
fun UserActivityLevelSectionStep(
    selectedActivityLevel: UserActivityLevel?,
    onActivityLevelSelected: (UserActivityLevel) -> Unit,
    onNextStep: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 65.dp)
    ) {
        Text(
            text = stringResource(R.string.your_activity_level),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        UserActivityLevel.entries.forEach { currentGoal ->
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onActivityLevelSelected(currentGoal) }
                    .shadow(
                        elevation = if (selectedActivityLevel == currentGoal) 8.dp else 0.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedActivityLevel == currentGoal)
                        MaterialTheme.colorScheme.primary
                    else Color.Transparent
                ),
                border = if (selectedActivityLevel != currentGoal)
                    BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
                else null,
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = currentGoal.displayName(),
                        color = if (selectedActivityLevel != currentGoal) MaterialTheme.colorScheme.outline
                        else MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = currentGoal.description(),
                        color = if (selectedActivityLevel != currentGoal) MaterialTheme.colorScheme.outline
                        else MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        ContinueButton(selectedActivityLevel!= null){
            onNextStep()
        }
    }
}

@Preview
@Composable
fun UserActivityLevelSectionStepPreview() {
    UserActivityLevelSectionStep(
        UserActivityLevel.ACTIVE, {}, {})
}