package com.alperenturker.englishcardgame.core.domain.repository

import com.alperenturker.englishcardgame.core.domain.model.Category
import com.alperenturker.englishcardgame.core.domain.model.Difficulty
import com.alperenturker.englishcardgame.core.domain.model.Question

interface QuestionRepository {
    suspend fun getCategories(): List<Category>
    suspend fun getNextQuestion(categoryId: String, difficulty: Difficulty): Question
}

