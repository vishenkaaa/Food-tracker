package com.example.presentation.features.auth.target

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import com.example.presentation.R
import com.example.presentation.common.ui.components.ContinueButton
import com.example.presentation.extensions.displayName

@Composable
fun GoalSelectionStep(
    selectedGoal: Goal?,
    onGoalSelected: (Goal) -> Unit,
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
            text = stringResource(R.string.what_is_your_goal),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Goal.entries.forEach { currentGoal ->
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onGoalSelected(currentGoal) }
                    .shadow(
                        elevation = if (selectedGoal == currentGoal) 8.dp else 0.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedGoal == currentGoal)
                        MaterialTheme.colorScheme.primary
                    else Color.Transparent
                ),
                border = if (selectedGoal != currentGoal)
                    BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
                else null,
            ) {
                Text(
                    text = currentGoal.displayName(),
                    modifier = Modifier.padding(16.dp),
                    color = if (selectedGoal != currentGoal) MaterialTheme.colorScheme.outline
                    else MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        ContinueButton(selectedGoal!= null){
            onNextStep()
        }
    }
}

@Preview
@Composable
fun GoalSelectionStepPreview() {
    GoalSelectionStep(
        Goal.GAIN, {}, {})
}