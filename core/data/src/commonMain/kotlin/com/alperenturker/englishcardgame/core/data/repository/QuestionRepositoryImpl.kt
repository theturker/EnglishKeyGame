package com.alperenturker.englishcardgame.core.data.repository

import com.alperenturker.englishcardgame.core.data.datasource.GroqRemoteDataSource
import com.alperenturker.englishcardgame.core.data.datasource.LocalQuestionDataSource
import com.alperenturker.englishcardgame.core.data.mapper.toDomain
import com.alperenturker.englishcardgame.core.domain.model.Category
import com.alperenturker.englishcardgame.core.domain.model.Difficulty
import com.alperenturker.englishcardgame.core.domain.model.Question
import com.alperenturker.englishcardgame.core.domain.repository.QuestionRepository

class QuestionRepositoryImpl(
    private val groqRemoteDataSource: GroqRemoteDataSource,
    private val localQuestionDataSource: LocalQuestionDataSource
) : QuestionRepository {
    
    override suspend fun getCategories(): List<Category> {
        // Sabit kategoriler - gelecekte Firebase'den çekilebilir
        return listOf(
            Category(id = "games", name = "Games", icon = "🎮"),
            Category(id = "movies", name = "Movies", icon = "🎬"),
            Category(id = "culture", name = "Culture", icon = "🎨"),
            Category(id = "science", name = "Science", icon = "🔬"),
            Category(id = "sports", name = "Sports", icon = "⚽"),
            Category(id = "travel", name = "Travel", icon = "✈️"),
            Category(id = "food", name = "Food", icon = "🍕"),
            Category(id = "technology", name = "Technology", icon = "💻")
        )
    }
    
    override suspend fun getNextQuestion(categoryId: String, difficulty: Difficulty): Question {
        // Önce local cache'den kontrol et
        val localQuestions = localQuestionDataSource.getQuestionsForCategory(categoryId, difficulty)
        
        // Eğer local'de soru varsa, random birini seç
        if (localQuestions.isNotEmpty()) {
            return localQuestions.random()
        }
        
        // Local'de soru yoksa Groq API'den yeni soru oluştur
        val category = getCategories().find { it.id == categoryId }
            ?: throw IllegalArgumentException("Category not found: $categoryId")
        
        val groqResponse = groqRemoteDataSource.generateQuestion(category, difficulty)
        val question = groqResponse.toDomain(categoryId)
        
        // Yeni soruyu local cache'e kaydet
        localQuestionDataSource.saveQuestion(question)
        
        return question
    }
}

