package com.example.domain.model.diary
import kotlinx.serialization.Serializable

@Serializable
data class Dish(
    val id: String = "",
    val title: String = "",
    val img: String = "",
    val kcal: Int = 0,
    val carb: Float = 0f,
    val protein: Float = 0f,
    val fats: Float = 0f,
    val amount: Float = 0f,
    val unit: UnitType = UnitType.GRAM,
)