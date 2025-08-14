package com.example.domain.model.diary

data class Dish(
    val id: String,
    val title: String,
    val img: String,
    val kcal: Int,
    val carb: Int,
    val protein: Int,
    val fats: Int,
    val amount: Int,
    val unit: UnitType,
)