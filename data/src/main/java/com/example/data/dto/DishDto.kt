package com.example.data.dto

import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.UnitType
import kotlinx.serialization.Serializable

@Serializable
data class DishDto(
    val id: String,
    val title: String,
    val kcal: Int,
    val carb: Float,
    val protein: Float,
    val fats: Float,
    val amount: Float,
    val unit: String
)

fun DishDto.toDish(): Dish = Dish(
    id = id,
    title = title,
    img = "",
    kcal = kcal,
    carb = carb,
    protein = protein,
    fats = fats,
    amount = amount,
    unit = UnitType.fromValue(unit)
)