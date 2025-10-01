package com.example.data.repository

import android.util.Log
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
import java.util.Locale
import javax.inject.Inject

class GptRepositoryImpl @Inject constructor(
    private val client: OkHttpClient,
) : GptRepository {

    private val apiKey: String
        get() = BuildConfig.OPENAI_API_KEY

    private val locale = if (Locale.getDefault().language == "uk") "Ukrainian" else "English"

    override suspend fun analyzeDishImage(imageBase64: String): Result<List<Dish>> {
        return try {
            val requestBody = createChatRequest(imageBase64)
            val response = executeRequest(requestBody)
            parseResponse(response)
        } catch (e: Exception) {
            Log.e("GptRepository", "Exception in analyzeDishImage: ${e.message}", e)
            Result.failure(e)
        }
    }

    private fun createChatRequest(imageBase64: String): ChatRequest {
        return ChatRequest(
            model = "gpt-4o",
            messages = listOf(
                Message(
                    role = "user",
                    content = listOf(
                        Content(
                            type = "text",
                            text = buildPrompt()
                        ),
                        Content(
                            type = "image_url",
                            image_url = Content.ImageUrl("data:image/jpeg;base64,$imageBase64")
                        )
                    )
                )
            )
        )
    }

    private fun buildPrompt(): String {
        return """
            You are an expert nutritionist. Analyze this food image and identify ALL visible dishes/food items with precise nutritional data.
            
            Output ONLY a valid JSON array in this exact format:
            
            [
              {
                "id": "uuid-v4-format",
                "title": "Food name in $locale",
                "kcal": 250,
                "carb": 15.5,
                "protein": 12.0,
                "fats": 8.5,
                "amount": 1.0,
                "unit": "piece"
              }
            ]
            
            CRITICAL UNIT SELECTION RULES:
            
            Use "piece" ONLY for items with standardized size that are naturally counted as individual units:
            - Whole fruits with standard size
            - Bread slices, rolls, buns
            - Individual bakery items
            - Eggs
            - Standard portion items like cutlets, pancakes
            - Any food item that has a consistent standard size when served
            
            Use "g" for:
            - Mixed dishes and salads
            - Irregular portions of meat, fish
            - Bulk foods like rice, pasta, cereals
            - Any food without standard individual sizing
            
            Use "ml" for small liquid items including soups, beverages, sauces
           
            Use "l" for large packaged liquids (bottles, cartons, jugs)
            
            NUTRITIONAL REQUIREMENTS:
            - Calculate nutrition per actual visible portion
            - Generate valid UUID v4 for each item
            - Round kcal to integer, others to 1 decimal place
            - Use standard nutritional databases for accuracy
            
            If no food visible: return []
            
            RESPOND WITH JSON ONLY - NO TEXT, MARKDOWN, OR EXPLANATIONS
        """.trimIndent()
    }

    private fun executeRequest(requestBody: ChatRequest): String {
        val jsonString = Json.encodeToString(requestBody)

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(jsonString.toRequestBody("application/json".toMediaType()))
            .build()

        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body.string()
                Log.e("GptRepository", "API Error: ${response.code}, Body: $errorBody")
                throw Exception("GPT API error: ${response.code} - $errorBody")
            }
            response.body.string()
        }
    }

    private fun parseResponse(responseBody: String): Result<List<Dish>> {
        val jsonObject = JSONObject(responseBody)
        val rawContent = jsonObject
            .getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")

        val cleanedContent = cleanJsonContent(rawContent)
        Log.d("GptRepository", "Cleaned content: $cleanedContent")

        if (cleanedContent.isEmpty() || cleanedContent == "[]") {
            return Result.success(emptyList())
        }

        val dishesDto = parseJsonContent(cleanedContent)
        val dishes = dishesDto.map { it.toDish() }

        return Result.success(dishes)
    }

    private fun parseJsonContent(content: String): List<DishDto> {
        return try {
            Json.decodeFromString<List<DishDto>>(content)
        } catch (e: Exception) {
            Log.e("GptRepository", "Initial JSON parsing failed: ${e.message}")
            val extractedJson = extractJsonFromText(content)
                ?: throw Exception("Could not extract valid JSON from GPT response: ${e.message}")

            try {
                Json.decodeFromString<List<DishDto>>(extractedJson)
            } catch (e2: Exception) {
                throw Exception("Failed to parse GPT response as JSON: ${e2.message}")
            }
        }
    }

    private fun cleanJsonContent(rawContent: String): String {
        return rawContent
            .trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .replace("```json", "")
            .replace("```", "")
            .trim()
    }

    private fun extractJsonFromText(text: String): String? {
        val startIndex = text.indexOf('[')
        val endIndex = text.lastIndexOf(']')

        return if (startIndex in 0..<endIndex) {
            text.substring(startIndex, endIndex + 1)
        } else {
            null
        }
    }
}