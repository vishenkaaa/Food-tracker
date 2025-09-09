package com.example.domain.usecase.gpt

import com.example.domain.model.diary.Dish
import com.example.domain.repository.GptRepository
import kotlinx.serialization.Serializable
import javax.inject.Inject

class AnalyzeDishImageUseCase @Inject constructor(
    private val gptRepository: GptRepository,
) {
    suspend operator fun invoke(imgUri: String):  Result<List<Dish>> {
        return gptRepository.analyzeDishImage(imgUri)
    }
}
