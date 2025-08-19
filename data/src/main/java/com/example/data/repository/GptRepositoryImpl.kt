package com.example.data.repository

import com.example.data.BuildConfig
import com.example.data.dto.ChatRequest
import com.example.data.dto.Content
import com.example.data.dto.DishDto
import com.example.data.dto.Message
import com.example.data.dto.toDish
import com.example.domain.model.diary.Dish
import com.example.domain.repository.GptRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject

class GptRepositoryImpl @Inject constructor(
    private val client: OkHttpClient,
) : GptRepository {

    private val apiKey: String
        get() = BuildConfig.OPENAI_API_KEY

    override suspend fun analyzeDishImage(imageBase64: String): Result<List<Dish>> {
        return try {
            val requestBody = ChatRequest(
                model = "gpt-4o",
                messages = listOf(
                    Message(
                        role = "user",
                        content = listOf(
                            Content(
                                type = "text",
                                text = """
                        Please analyze this food image and return a JSON array of detected dishes with the following format:

                        [
                          {
                            "title": "Dish name",
                            "kcal": "Calories in kcal",
                            "carb": "Carbohydrates in grams",
                            "protein": "Protein in grams",
                            "fats": "Fats in grams",
                            "amount": "Quantity value",
                            "unit": "Unit of measurement (g, ml, l, pcs)"
                          }
                        ]

                        If you cannot identify any dishes with confidence, respond with an empty JSON array: []
                        IMPORTANT: Respond only with the JSON object or array — do not include any additional text.
                    """.trimIndent()
                            ),
                            Content(
                                type = "image_url",
                                image_url = "data:image/jpeg;base64,$imageBase64"
                            )
                        )
                    )
                )
            )

            val jsonString = Json.encodeToString(requestBody)


            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(jsonString.toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return Result.failure(Exception("GPT error: ${response.code}"))

                val body = response.body.string()
                val jsonObject = JSONObject(body)
                val content = jsonObject
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

                val dishesDto = Json.decodeFromString<List<DishDto>>(content)
                val dishes = dishesDto.map { it.toDish() }
                Result.success(dishes)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}