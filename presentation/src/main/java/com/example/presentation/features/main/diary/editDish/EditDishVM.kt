package com.example.presentation.features.main.diary.editDish

import com.example.domain.extension.roundTo1Decimal
import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.MealType
import com.example.domain.model.diary.NutritionData
import com.example.domain.model.diary.UnitType
import com.example.presentation.arch.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class EditDishVM @Inject constructor() : BaseViewModel() {
    private val _state = MutableStateFlow(EditDishState())
    val state: StateFlow<EditDishState> = _state.asStateFlow()

    fun initialize(dish: Dish, mealType: MealType) {
        val initialUnit = UnitType.fromValue(dish.unit.value)
        val availableUnits = getAvailableUnits(dish.unit)

        _state.value = EditDishState(
            dish = dish,
            mealType = mealType,
            amount = dish.amount.toString(),
            selectedUnit = initialUnit,
            availableUnits = availableUnits
        )

        calculateNutrition()
    }

    fun updateAmount(newAmount: String) {
        if (newAmount.isEmpty() || newAmount.matches(Regex("^\\d*\\.?\\d*$"))) {
            _state.update { it.copy(amount = newAmount) }
            calculateNutrition()
        }
    }

    fun updateUnit(newUnit: UnitType) {
        _state.update { it.copy(selectedUnit = newUnit) }
        calculateNutrition()
    }

    fun updateMealType(newMealType: MealType) {
        _state.update { it.copy(mealType = newMealType) }
    }

    private fun calculateNutrition() {
        val currentState = _state.value
        val currentAmount = currentState.amount.toFloatOrNull()?: 0f
        val originalAmount = currentState.dish.amount
        val originalUnit = currentState.dish.unit

        val convertedAmount = when {
            currentState.selectedUnit == originalUnit -> currentAmount

            currentState.selectedUnit == UnitType.LITER && originalUnit == UnitType.MILLILITER ->
                currentAmount * 1000f
            currentState.selectedUnit == UnitType.MILLILITER && originalUnit == UnitType.LITER ->
                currentAmount / 1000f

            else -> currentAmount
        }

        val ratio = if (originalAmount != 0f) convertedAmount / originalAmount else 0f

        val nutrition = NutritionData(
            calories = (currentState.dish.kcal * ratio).toInt(),
            protein = currentState.dish.protein * ratio,
            carb = currentState.dish.carb * ratio,
            fat = currentState.dish.fats * ratio
        )

        _state.update {
            it.copy(
                currentNutrition = nutrition,
            )
        }
    }

    private fun getAvailableUnits(originalUnit: UnitType): List<UnitType> {
        return when (originalUnit) {
            UnitType.GRAM -> listOf(UnitType.GRAM)
            UnitType.MILLILITER -> listOf(UnitType.MILLILITER, UnitType.LITER)
            UnitType.LITER -> listOf(UnitType.MILLILITER, UnitType.LITER)
            UnitType.PIECE -> listOf(UnitType.PIECE)
        }
    }

    fun createUpdatedDish(): Dish {
        val currentState = _state.value
        val amount = currentState.amount.toFloatOrNull()?.roundTo1Decimal() ?: 0f
        return currentState.dish.copy(
            amount = amount,
            unit = currentState.selectedUnit,
            kcal = currentState.currentNutrition.calories,
            protein = currentState.currentNutrition.protein.roundTo1Decimal()!!,
            carb = currentState.currentNutrition.carb.roundTo1Decimal()!!,
            fats = currentState.currentNutrition.fat.roundTo1Decimal()!!
        )
    }
}

data class EditDishState(
    val dish: Dish = Dish(),
    val mealType: MealType = MealType.BREAKFAST,
    val amount: String = "",
    val selectedUnit: UnitType = UnitType.GRAM,
    val currentNutrition: NutritionData = NutritionData(0, 0f, 0f, 0f),
    val availableUnits: List<UnitType> = emptyList()
)