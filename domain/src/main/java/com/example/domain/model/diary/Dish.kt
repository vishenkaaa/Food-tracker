package com.example.domain.model.diary
import kotlinx.serialization.Serializable

@Serializable
data class Dish(
    val id: String = "",
    val title: String = "",
    val img: String = "",
    val kcal: Int = 0,
    val carb: Int = 0,
    val protein: Int = 0,
    val fats: Int = 0,
    val amount: Int = 0,
    val unit: UnitType = UnitType.GRAM,
)