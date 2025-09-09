package com.example.data.mapper

import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.UnitType

object DishModelMapper {
    private const val DISH_TITLE_KEY = "title"
    private const val DISH_IMG_KEY = "img"
    private const val DISH_KCAL_KEY = "kcal"
    private const val DISH_CARB_KEY = "carb"
    private const val DISH_PROTEIN_KEY = "protein"
    private const val DISH_FATS_KEY = "fats"
    private const val DISH_AMOUNT_KEY = "amount"
    private const val DISH_UNIT_KEY = "unit"

    // Dish в Map для Firebase
    fun dishToMap(dish: Dish): Map<String, Any> {
        return mapOf(
            DISH_TITLE_KEY to dish.title,
            DISH_IMG_KEY to dish.img,
            DISH_KCAL_KEY to dish.kcal,
            DISH_CARB_KEY to dish.carb,
            DISH_PROTEIN_KEY to dish.protein,
            DISH_FATS_KEY to dish.fats,
            DISH_AMOUNT_KEY to dish.amount,
            DISH_UNIT_KEY to dish.unit.value
        )
    }

    // Map з Firebase в Dish
    fun mapToDish(data: Map<String, Any>, dishId: String): Dish {
        return Dish(
            id = dishId,
            title = data[DISH_TITLE_KEY] as? String ?: "",
            img = data[DISH_IMG_KEY] as? String ?: "",
            kcal = (data[DISH_KCAL_KEY] as? Number)?.toInt() ?: 0,
            carb = (data[DISH_CARB_KEY] as? Number)?.toFloat() ?: 0f,
            protein = (data[DISH_PROTEIN_KEY] as? Number)?.toFloat() ?: 0f,
            fats = (data[DISH_FATS_KEY] as? Number)?.toFloat() ?: 0f,
            amount = (data[DISH_AMOUNT_KEY] as? Number)?.toFloat() ?: 0f,
            unit = data[DISH_UNIT_KEY]?.toString()?.let {
                UnitType.fromValue(it)
            } ?: UnitType.GRAM
        )
    }
}