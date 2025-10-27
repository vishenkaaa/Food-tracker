package com.example.presentation.features.main.diary.components

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.UnitType
import com.example.presentation.R
import com.example.presentation.common.ui.modifiers.softShadow
import com.example.presentation.extensions.displayName

@Composable
fun DishCard(
    dish: Dish,
) {
    val finishDishAmount = if (dish.unit == UnitType.PIECE) dish.amount.toInt()
    else dish.amount

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .softShadow(
                color = MaterialTheme.colorScheme.surfaceVariant,
                blurRadius = 12.dp,
                offsetY = 1.dp,
                offsetX = 1.dp,
                cornerRadius = 16.dp
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = dish.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.kcal_value, dish.kcal),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = "$finishDishAmount ${dish.unit.displayName()}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onBackground.copy(0.1f),
                thickness = 0.5.dp,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
            )

            MacroNutrientsSmallSection(dish.protein, dish.fats, dish.carb)
        }
    }
}