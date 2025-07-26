package com.example.presentation.features.auth.target

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.domain.model.Goal
import com.example.presentation.extensions.displayName

@Composable
fun GoalSelectionStep(
    selectedGoal: Goal?,
    onGoalSelected: (Goal) -> Unit,
    onNextStep: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Яка ваша мета?",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Goal.entries.forEach { currentGoal ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onGoalSelected(currentGoal) },
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedGoal == currentGoal)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else MaterialTheme.colorScheme.surface
                ),
                border = if (selectedGoal == currentGoal)
                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                else null
            ) {
                Text(
                    text = currentGoal.displayName(),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Button(
            onClick = {onNextStep() },
            modifier = Modifier
                .padding(end = 16.dp),
            enabled = selectedGoal!= null
        ) {
            Text(
                "Продовжити"
            )
        }
    }
}