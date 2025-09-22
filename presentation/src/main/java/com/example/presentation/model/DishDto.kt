package com.example.presentation.model

import android.os.Parcelable
import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.UnitType
import kotlinx.parcelize.Parcelize

@Parcelize
data class DishDto(
    val id: String,
    val title: String,
    val img: String,
    val kcal: Int,
    val carb: Float,
    val protein: Float,
    val fats: Float,
    val amount: Float,
    val unit: String
) : Parcelable

fun Dish.toDto(): DishDto = DishDto(
    id = id,
    title = title,
    img = img,
    kcal = kcal,
    carb = carb,
    protein = protein,
    fats = fats,
    amount = amount,
    unit = unit.value
)

fun DishDto.toDomain(): Dish = Dish(
    id = id,
    title = title,
    img = img,
    kcal = kcal,
    carb = carb,
    protein = protein,
    fats = fats,
    amount = amount,
    unit = UnitType.fromValue(unit)
)

fun List<Dish>.toDto(): List<DishDto> = map { it.toDto() }
fun List<DishDto>.toDomain(): List<Dish> = map { it.toDomain() }