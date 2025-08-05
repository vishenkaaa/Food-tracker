package com.example.presentation.features.auth.onboarding.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.domain.model.MacroNutrients
import com.example.presentation.R
import com.example.presentation.common.utils.BMICalculator

@Composable
fun ResultStep(
    macroNutrients: MacroNutrients,
    bmi: Float,
    targetCalories: Int,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.recommendations_ready),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.personalized_recommendations_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(40.dp))
        BmiCard(bmi)

        Spacer(Modifier.height(20.dp))
        CaloriesCard(targetCalories)

        Spacer(Modifier.height(20.dp))
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ){
            MacroNutrientsCard(macroNutrients.proteins,
                stringResource(R.string.proteins), painterResource(R.drawable.eggcrack))
            MacroNutrientsCard(macroNutrients.carbs,
                stringResource(R.string.carbohydrates), painterResource(R.drawable.grains))
            MacroNutrientsCard(macroNutrients.fats,
                stringResource(R.string.fats), painterResource(R.drawable.avocado))
        }
    }
}

@Composable
fun CaloriesCard(
    targetCalories: Int
){
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false,
                ambientColor = MaterialTheme.colorScheme.onBackground.copy(0.15f)
            ),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    painter = painterResource(R.drawable.fire),
                    contentDescription = "Fire",
                    tint = Color.Unspecified
                )
                Text(
                    text = stringResource(R.string.calories_format, targetCalories),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.daily_calories_recommendation),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BmiCard(
    bmi: Float,
){
    val context = LocalContext.current
    val bmiCategory = BMICalculator.getBMICategory(bmi, context)
    val progress = BMICalculator.getBMIProgress(bmi)

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false,
                ambientColor = MaterialTheme.colorScheme.onBackground.copy(0.15f)
            ),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.bmi_format).format(bmi),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                trackColor = bmiCategory.color.copy(0.2f),
                color = bmiCategory.color
            )
            Text(
                text = stringResource(R.string.bmi_category_format, bmiCategory.label),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(0.6f),
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun MacroNutrientsCard(
    value: Int,
    name: String,
    icon: Painter
){
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false,
                ambientColor = MaterialTheme.colorScheme.onBackground.copy(0.15f)
            ),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = "Fire",
                tint = Color.Unspecified
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.grams_format, value),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.6f),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ResultStepPreview(){
    ResultStep(
        MacroNutrients(), 21f, 1000
    )
}