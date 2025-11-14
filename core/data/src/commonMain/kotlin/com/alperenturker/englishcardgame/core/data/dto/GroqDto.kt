package com.alperenturker.englishcardgame.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class GroqRequest(
    val model: String = "llama-3.3-70b-versatile", // Updated to latest model
    val messages: List<GroqMessage>,
    val temperature: Double = 0.7,
    val response_format: GroqResponseFormat = GroqResponseFormat()
)

@Serializable
data class GroqMessage(
    val role: String,
    val content: String
)

@Serializable
data class GroqResponseFormat(
    val type: String = "json_object"
)

@Serializable
data class GroqResponse(
    val id: String? = null,
    val `object`: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val choices: List<GroqChoice>
)

@Serializable
data class GroqChoice(
    val message: GroqMessage
)

@Serializable
data class GroqQuestionResponse(
    val question: String,
    val options: List<GroqOption> = emptyList(),
    val choices: List<GroqOption> = emptyList(), // Alternative field name
    val difficulty: String = "EASY"
) {
    // Get options from either 'options' or 'choices' field
    // Note: Cannot use getOptions() because it conflicts with property getter
    fun getOptionsList(): List<GroqOption> = if (options.isNotEmpty()) options else choices
}

@Serializable
data class GroqOption(
    val id: String,
    val text: String,
    val isCorrect: Boolean
)

