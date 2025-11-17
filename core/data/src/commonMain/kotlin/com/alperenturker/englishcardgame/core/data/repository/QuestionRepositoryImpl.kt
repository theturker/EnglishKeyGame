package com.alperenturker.englishcardgame.core.data.repository

import com.alperenturker.englishcardgame.core.data.datasource.GroqRemoteDataSource
import com.alperenturker.englishcardgame.core.data.datasource.LocalQuestionDataSource
import com.alperenturker.englishcardgame.core.data.mapper.toDomain
import com.alperenturker.englishcardgame.core.domain.model.Category
import com.alperenturker.englishcardgame.core.domain.model.Difficulty
import com.alperenturker.englishcardgame.core.domain.model.Question
import com.alperenturker.englishcardgame.core.domain.repository.QuestionRepository
import com.alperenturker.englishcardgame.core.domain.repository.UserProgressRepository

class QuestionRepositoryImpl(
    private val groqRemoteDataSource: GroqRemoteDataSource,
    private val localQuestionDataSource: LocalQuestionDataSource,
    private val userProgressRepository: UserProgressRepository
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
        
        // Kullanıcının daha önce cevapladığı soruları al (kalıcı)
        val userProgress = userProgressRepository.getProgress(categoryId)
        val answeredQuestionIds = userProgress?.answeredQuestionIds ?: emptySet()
        
        // Hem session'da sorulan hem de kalıcı olarak cevaplanan soruları birleştir
        val allExcludedIds = askedIds + answeredQuestionIds
        
        val category = getCategories().find { it.id == categoryId }
            ?: throw IllegalArgumentException("Category not found: $categoryId")
        
        // Her zaman Groq API'den yeni soru oluştur (farklı sorular garantili)
        // Ancak aynı soru tekrar gelirse cache'den kullan
        var maxRetries = 5 // Artırıldı çünkü cevaplanan sorular da filtreleniyor
        var question: Question? = null
        
        while (maxRetries > 0 && question == null) {
            val groqResponse = groqRemoteDataSource.generateQuestion(category, difficulty)
            val newQuestion = groqResponse.toDomain(categoryId)
            
            // Bu soru daha önce sorulmuş veya cevaplanmış mı kontrol et
            // Artık ID soru metninden hash'lendiği için aynı soru her zaman aynı ID'ye sahip
            if (newQuestion.id !in allExcludedIds) {
                // Yeni ve benzersiz soru
                localQuestionDataSource.saveQuestion(newQuestion)
                askedIds.add(newQuestion.id)
                question = newQuestion
            } else {
                // Bu soru daha önce sorulmuş veya cevaplanmış, yeni soru üret
                maxRetries--
            }
        }
        
        // Eğer hala soru yoksa (çok nadir durum), cache'den sorulmamış ve cevaplanmamış bir soru seç
        if (question == null) {
            val localQuestions = localQuestionDataSource.getQuestionsForCategory(categoryId, difficulty)
            val unansweredQuestions = localQuestions.filter { it.id !in allExcludedIds }
            if (unansweredQuestions.isNotEmpty()) {
                question = unansweredQuestions.random()
                askedIds.add(question.id)
            } else {
                // Cache'de de soru yoksa, Groq'dan geleni direkt kullan (son çare)
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

