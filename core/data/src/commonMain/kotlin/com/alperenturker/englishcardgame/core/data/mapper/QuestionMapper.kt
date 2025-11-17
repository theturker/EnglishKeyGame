package com.alperenturker.englishcardgame.core.data.mapper

import com.alperenturker.englishcardgame.core.common.IdGenerator
import com.alperenturker.englishcardgame.core.data.dto.GroqOption
import com.alperenturker.englishcardgame.core.data.dto.GroqQuestionResponse
import com.alperenturker.englishcardgame.core.domain.model.AnswerOption
import com.alperenturker.englishcardgame.core.domain.model.Difficulty
import com.alperenturker.englishcardgame.core.domain.model.Question

fun GroqQuestionResponse.toDomain(categoryId: String): Question {
    val difficulty = try {
        Difficulty.valueOf(difficulty.uppercase())
    } catch (e: Exception) {
        Difficulty.EASY
    }
    
    val optionList = getOptionsList()
    if (optionList.isEmpty()) {
        throw IllegalArgumentException("No options found in Groq response")
    }
    
    // Soru ID'sini soru metninden hash'leyerek oluştur
    // Böylece aynı soru metni her zaman aynı ID'ye sahip olur
    val questionText = question.trim()
    val questionId = "q_${questionText.hashCode().toString().replace("-", "n")}"
    
    return Question(
        id = questionId,
        categoryId = categoryId,
        text = questionText,
        options = optionList.map { it.toDomain() },
        difficulty = difficulty
    )
}

fun GroqOption.toDomain(): AnswerOption {
    return AnswerOption(
        id = id,
        text = text,
        isCorrect = isCorrect
    )
}

