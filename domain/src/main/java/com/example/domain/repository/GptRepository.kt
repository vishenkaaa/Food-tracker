package com.example.domain.repository

import com.example.domain.model.diary.Dish

interface GptRepository {
    suspend fun analyzeDishImage(imageBase64: String): Result<List<Dish>>
}