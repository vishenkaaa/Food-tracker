package com.example.data.dto

import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.UnitType

data class DishDto(
    val title: String,
    val kcal: Int,
    val carb: Float,
    val protein: Float,
    val fats: Float,
    val amount: Float,
    val unit: String
)

fun DishDto.toDish(): Dish = Dish(
    id = "",
    title = title,
    img = "",
    kcal = kcal,
    carb = carb,
    protein = protein,
    fats = fats,
    amount = amount,
    unit = UnitType.fromValue(unit) ?: UnitType.GRAM
)