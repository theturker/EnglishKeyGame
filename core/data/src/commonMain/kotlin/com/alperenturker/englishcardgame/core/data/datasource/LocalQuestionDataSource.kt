package com.alperenturker.englishcardgame.core.data.datasource

import com.alperenturker.englishcardgame.core.domain.model.Difficulty
import com.alperenturker.englishcardgame.core.domain.model.Question

/**
 * Local question storage (for future Firebase sync)
 * For now, this will be a simple in-memory cache
 */
interface LocalQuestionDataSource {
    suspend fun saveQuestion(question: Question)
    suspend fun getQuestionsForCategory(categoryId: String, difficulty: Difficulty): List<Question>
    suspend fun clearCache()
}

class LocalQuestionDataSourceImpl : LocalQuestionDataSource {
    private val questionCache = mutableMapOf<String, MutableList<Question>>()
    
    override suspend fun saveQuestion(question: Question) {
        val key = "${question.categoryId}_${question.difficulty}"
        questionCache.getOrPut(key) { mutableListOf() }.add(question)
    }
    
    override suspend fun getQuestionsForCategory(categoryId: String, difficulty: Difficulty): List<Question> {
        val key = "${categoryId}_${difficulty}"
        return questionCache[key]?.toList() ?: emptyList()
    }
    
    override suspend fun clearCache() {
        questionCache.clear()
    }
}

