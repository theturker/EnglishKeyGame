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
    
    // Her kategori+difficulty için bu session'da sorulmuş soruların ID'lerini takip et
    private val askedQuestionIds = mutableMapOf<String, MutableSet<String>>()
    
    private fun getKey(categoryId: String, difficulty: Difficulty): String {
        return "${categoryId}_${difficulty}"
    }
    
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
        val key = getKey(categoryId, difficulty)
        val askedIds = askedQuestionIds.getOrPut(key) { mutableSetOf() }
        
        val category = getCategories().find { it.id == categoryId }
            ?: throw IllegalArgumentException("Category not found: $categoryId")
        
        // Her zaman Groq API'den yeni soru oluştur (farklı sorular garantili)
        // Ancak aynı soru tekrar gelirse cache'den kullan
        var maxRetries = 3
        var question: Question? = null
        
        while (maxRetries > 0 && question == null) {
            val groqResponse = groqRemoteDataSource.generateQuestion(category, difficulty)
            val newQuestion = groqResponse.toDomain(categoryId)
            
            // Bu soru daha önce sorulmuş mu kontrol et
            if (newQuestion.id !in askedIds) {
                // Aynı metin içeriğine sahip soru var mı kontrol et (duplicate)
                val localQuestions = localQuestionDataSource.getQuestionsForCategory(categoryId, difficulty)
                val existingQuestion = localQuestions.find { 
                    it.text.trim().equals(newQuestion.text.trim(), ignoreCase = true)
                }
                
                if (existingQuestion == null) {
                    // Yeni ve benzersiz soru
                    localQuestionDataSource.saveQuestion(newQuestion)
                    askedIds.add(newQuestion.id)
                    question = newQuestion
                } else {
                    // Aynı soru metni var ama farklı ID - existing olanı kullan
                    askedIds.add(existingQuestion.id)
                    question = existingQuestion
                }
            } else {
                // Bu ID daha önce sorulmuş, tekrar deneme sayısını azalt
                maxRetries--
            }
        }
        
        // Eğer hala soru yoksa (çok nadir durum), cache'den sorulmamış bir soru seç
        if (question == null) {
            val localQuestions = localQuestionDataSource.getQuestionsForCategory(categoryId, difficulty)
            val unansweredQuestions = localQuestions.filter { it.id !in askedIds }
            if (unansweredQuestions.isNotEmpty()) {
                question = unansweredQuestions.random()
                askedIds.add(question.id)
            } else {
                // Cache'de de soru yoksa, Groq'dan geleni direkt kullan
                val groqResponse = groqRemoteDataSource.generateQuestion(category, difficulty)
                question = groqResponse.toDomain(categoryId)
                localQuestionDataSource.saveQuestion(question)
                askedIds.add(question.id)
            }
        }
        
        return question!!
    }
    
    // Quiz başladığında çağrılır (soru takibini sıfırlamak için)
    fun resetQuestionTracking(categoryId: String, difficulty: Difficulty) {
        val key = getKey(categoryId, difficulty)
        askedQuestionIds[key]?.clear()
    }
    
    // Tüm tracking'i sıfırla
    fun resetAllTracking() {
        askedQuestionIds.clear()
    }
}

