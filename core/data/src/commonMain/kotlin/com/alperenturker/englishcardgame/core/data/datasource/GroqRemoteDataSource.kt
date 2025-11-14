package com.alperenturker.englishcardgame.core.data.datasource

import com.alperenturker.englishcardgame.core.data.config.GroqApiKeyProvider
import com.alperenturker.englishcardgame.core.data.dto.*
import com.alperenturker.englishcardgame.core.domain.model.Category
import com.alperenturker.englishcardgame.core.domain.model.Difficulty
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class GroqRemoteDataSource(
    private val httpClient: HttpClient,
    private val apiKeyProvider: GroqApiKeyProvider
) {
    suspend fun generateQuestion(category: Category, difficulty: Difficulty): GroqQuestionResponse {
        val prompt = buildPrompt(category, difficulty)
        
        val requestBody = GroqRequest(
            model = "llama-3.3-70b-versatile", // Updated to latest model
            messages = listOf(
                GroqMessage(role = "user", content = prompt)
            ),
            temperature = 0.7,
            response_format = GroqResponseFormat(type = "json_object")
        )
        
        // Serialize request manually to ensure all fields are included
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true // IMPORTANT: encode defaults so model field is included
        }
        
        val requestBodyJson = json.encodeToString(requestBody)
        
        val response: HttpResponse = httpClient.post("https://api.groq.com/openai/v1/chat/completions") {
            header("Authorization", "Bearer ${apiKeyProvider.getApiKey()}")
            header("Content-Type", "application/json")
            setBody(requestBodyJson)
        }
        
        // Get raw response body as string (bypass ContentNegotiation)
        val responseBody = response.bodyAsText()
        
        // Check response status
        if (!response.status.isSuccess()) {
            throw IllegalStateException(
                "Groq API returned error status ${response.status.value}. " +
                "Response body: ${responseBody.take(500)}"
            )
        }
        
        // Check if response is an error (Groq API sometimes returns 200 with error object)
        if (responseBody.contains("\"error\"")) {
            throw IllegalStateException(
                "Groq API returned an error. " +
                "Response body: ${responseBody.take(500)}"
            )
        }
        
        // Try to parse as GroqResponse
        val jsonParser = Json {
            ignoreUnknownKeys = true // Ignore unknown fields like 'id', 'object', 'created', 'model'
            isLenient = true
            encodeDefaults = false
        }
        
        val groqResponse = try {
            jsonParser.decodeFromString<GroqResponse>(responseBody)
        } catch (e: Exception) {
            throw IllegalStateException(
                "Failed to parse Groq API response structure. " +
                "Error: ${e.message}. " +
                "Response body: ${responseBody.take(500)}"
            )
        }
        
        val content = groqResponse.choices.firstOrNull()?.message?.content
            ?: throw IllegalStateException(
                "Empty or invalid response from Groq API. " +
                "Response body: ${responseBody.take(500)}"
            )
        
        // Clean content if it contains markdown code blocks
        val cleanedContent = content.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
        
        return try {
            Json.decodeFromString<GroqQuestionResponse>(cleanedContent)
        } catch (e: Exception) {
            throw IllegalStateException(
                "Failed to parse question content from Groq response. " +
                "Error: ${e.message}. " +
                "Content: ${cleanedContent.take(500)}"
            )
        }
    }
    
    private fun buildPrompt(category: Category, difficulty: Difficulty): String {
        return """
            Generate an English vocabulary or grammar question about ${category.name} category with ${difficulty.name} difficulty level.
            
            The question should be educational and appropriate for English learning.
            
            Return a JSON object with the following exact structure:
            {
                "question": "Your question text here",
                "options": [
                    {"id": "a", "text": "Option A text", "isCorrect": false},
                    {"id": "b", "text": "Option B text", "isCorrect": true},
                    {"id": "c", "text": "Option C text", "isCorrect": false},
                    {"id": "d", "text": "Option D text", "isCorrect": false}
                ],
                "difficulty": "${difficulty.name}"
            }
            
            Make sure:
            - The question is clear and concise
            - Exactly one option has "isCorrect": true
            - All options are plausible but only one is correct
            - Return ONLY the JSON object, no other text
        """.trimIndent()
    }
}

