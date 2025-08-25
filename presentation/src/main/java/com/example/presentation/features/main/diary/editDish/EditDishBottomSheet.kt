package com.example.presentation.features.main.diary.editDish

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.MealType
import com.example.domain.model.diary.NutritionData
import com.example.domain.model.diary.UnitType
import com.example.presentation.R
import com.example.presentation.common.ui.components.CustomButton
import com.example.presentation.common.ui.modifiers.softShadow
import com.example.presentation.extensions.displayName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDishBottomSheet(
    dish: Dish,
    mealType: MealType,
    onSave: (Dish, MealType) -> Unit,
    onDismiss: () -> Unit,
    viewModel: EditDishVM = hiltViewModel()
) {
   val state = viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(dish, mealType) {
        viewModel.initialize(dish, mealType)
    }

    LaunchedEffect(Unit) {
        viewModel.showToast.collect {
            Toast.makeText(context, "Кількість має бути більшою за 0", Toast.LENGTH_SHORT).show()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = {
            Box(
            modifier = Modifier
                .padding(top = 20.dp, bottom = 15.dp)
                .width(48.dp)
                .height(4.dp)
                .background(
                    MaterialTheme.colorScheme.onBackground,
                    RoundedCornerShape(2.dp)
                )
        )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dish.title,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            AmountAndUnitRow(
                amount = state.value.amount,
                onAmountChange = viewModel::updateAmount,
                selectedUnit = state.value.selectedUnit,
                availableUnits = state.value.availableUnits,
                onUnitChange = viewModel::updateUnit
            )

            Spacer(modifier = Modifier.height(16.dp))

            MealTypeDropdown(
                selectedMealType = state.value.mealType,
                onMealTypeChange = { viewModel.updateMealType(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            NutritionGrid(state.value.currentNutrition)

            Spacer(modifier = Modifier.height(32.dp))

            CustomButton(
                text = stringResource(R.string.save),
                onClick = {
                    val updatedDish = viewModel.createUpdatedDish()
                    if(updatedDish!=null) onSave(updatedDish, state.value.mealType)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AmountAndUnitRow(
    amount: String,
    onAmountChange: (String) -> Unit,
    selectedUnit: UnitType,
    availableUnits: List<UnitType>,
    onUnitChange: (UnitType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StyledTextField(
            value = amount,
            onValueChange = onAmountChange,
            modifier = Modifier.weight(0.3f),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            )
        )

        UnitDropdown(
            selectedUnit = selectedUnit,
            onUnitChange = onUnitChange,
            modifier = Modifier.weight(0.7f),
            availableUnits = availableUnits
        )
    }
}

@Composable
private fun UnitDropdown(
    selectedUnit: UnitType,
    availableUnits: List<UnitType>,
    onUnitChange: (UnitType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    StyledDropdownMenu(
        value = selectedUnit.displayName(),
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
        items = availableUnits,
        itemText = { it.displayName() },
        onItemClick = { unit ->
            onUnitChange(unit)
            expanded = false
        }
    )
}

@Composable
private fun MealTypeDropdown(
    selectedMealType: MealType,
    onMealTypeChange: (MealType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    StyledDropdownMenu(
        value = selectedMealType.displayName(),
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth(),
        items = MealType.entries,
        itemText = { it.displayName() },
        onItemClick = { mealType ->
            onMealTypeChange(mealType)
            expanded = false
        },
        showRotatingIcon = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> StyledDropdownMenu(
    value: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    items: List<T>,
    itemText: @Composable (T) -> String,
    onItemClick: (T) -> Unit,
    showRotatingIcon: Boolean = false
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier
    ) {
        StyledTextField(
            value = value,
            onValueChange = { },
            readOnly = true,
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            trailingIcon = {
                if(items.size>1) Icon(
                    painter = painterResource(R.drawable.down_arrow),
                    contentDescription = "Dropdown arrow",
                    tint = Color.Unspecified,
                    modifier = if (showRotatingIcon) {
                        Modifier.rotate(if (expanded) 180f else 0f)
                    } else Modifier
                )
            }
        )

        if(items.size>1)
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = itemText(item),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        onClick = { onItemClick(item) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        colors = MenuDefaults.itemColors(
                            textColor = MaterialTheme.colorScheme.onBackground,
                            leadingIconColor = MaterialTheme.colorScheme.onBackground,
                            trailingIconColor = MaterialTheme.colorScheme.onBackground,
                        )
                    )
                }
            }
    }
}

@Composable
private fun StyledTextField(
    value: String,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onBackground
        ),
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        trailingIcon = trailingIcon,
        modifier = modifier
            .softShadow(
                color = MaterialTheme.colorScheme.onBackground.copy(0.05f),
                offsetX = 1.dp,
                offsetY = 1.dp,
                blurRadius = 12.dp,
                cornerRadius = 16.dp
            ),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

@Composable
private fun NutritionGrid(nutrition: NutritionData) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NutritionItem(
                icon = painterResource(R.drawable.fire),
                label = stringResource(R.string.calories),
                value = stringResource(R.string.kcal_value, nutrition.calories),
                modifier = Modifier.weight(0.5f)
            )
            NutritionItem(
                icon = painterResource(R.drawable.eggcrack),
                label = stringResource(R.string.protein),
                value = stringResource(R.string.grams_format, nutrition.protein),
                modifier = Modifier.weight(0.5f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NutritionItem(
                icon = painterResource(R.drawable.grains),
                label = stringResource(R.string.carb),
                value = stringResource(R.string.grams_format, nutrition.carb),
                modifier = Modifier.weight(0.5f)
            )
            NutritionItem(
                icon = painterResource(R.drawable.avocado),
                label = stringResource(R.string.fat),
                value = stringResource(R.string.grams_format, nutrition.fat),
                modifier = Modifier.weight(0.5f)
            )
        }
    }
}

@Composable
private fun NutritionItem(
    icon: Painter,
    label: String,
    value: String,
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .softShadow(
                color = MaterialTheme.colorScheme.onBackground.copy(0.05f),
                blurRadius = 12.dp,
                offsetX = 0.dp,
                offsetY = 4.dp,
                cornerRadius = 16.dp
            )
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(vertical = 18.dp, horizontal = 28.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = icon,
            contentDescription = label,
            tint = Color.Unspecified,
            modifier = Modifier.size(24.dp)
        )

        Spacer(Modifier.width(8.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}