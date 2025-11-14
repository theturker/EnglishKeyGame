package com.alperenturker.englishcardgame.feature.quiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperenturker.englishcardgame.core.domain.model.AnswerOption
import com.alperenturker.englishcardgame.core.domain.model.Category
import com.alperenturker.englishcardgame.core.domain.model.Difficulty
import com.alperenturker.englishcardgame.core.domain.model.Question
import com.alperenturker.englishcardgame.core.domain.usecase.GetNextQuestionUseCase
import com.alperenturker.englishcardgame.core.domain.usecase.GetUserProgressUseCase
import com.alperenturker.englishcardgame.core.domain.usecase.SubmitAnswerUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizUiState(
    val isLoading: Boolean = false,
    val category: Category? = null,
    val currentQuestion: Question? = null,
    val selectedAnswerId: String? = null,
    val isAnswerCorrect: Boolean? = null,
    val showFeedback: Boolean = false,
    val score: Int = 0,
    val totalAnswered: Int = 0,
    val difficulty: Difficulty = Difficulty.EASY,
    val errorMessage: String? = null
)

class QuizViewModel(
    private val getNextQuestionUseCase: GetNextQuestionUseCase,
    private val submitAnswerUseCase: SubmitAnswerUseCase,
    private val getUserProgressUseCase: GetUserProgressUseCase,
    categoryId: String,
    categoryName: String,
    categoryIcon: String?
) : ViewModel() {
    
    private val category = Category(categoryId, categoryName, categoryIcon)
    
    private val _uiState = MutableStateFlow(QuizUiState(category = category))
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()
    
    init {
        loadInitialProgress()
        loadNextQuestion()
    }
    
    private fun loadInitialProgress() {
        viewModelScope.launch {
            val progress = getUserProgressUseCase(category.id)
            progress?.let {
                _uiState.value = _uiState.value.copy(
                    score = it.totalCorrect,
                    totalAnswered = it.totalAnswered,
                    difficulty = it.currentDifficulty
                )
            }
        }
    }
    
    fun loadNextQuestion() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                selectedAnswerId = null,
                isAnswerCorrect = null,
                showFeedback = false
            )
            try {
                val question = getNextQuestionUseCase(category.id)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentQuestion = question,
                    difficulty = question.difficulty
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Soru yüklenirken bir hata oluştu"
                )
            }
        }
    }
    
    fun onAnswerSelected(answer: AnswerOption) {
        if (_uiState.value.selectedAnswerId != null || _uiState.value.isLoading) {
            return // Zaten bir cevap seçildi veya yükleniyor
        }
        
        val question = _uiState.value.currentQuestion ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                selectedAnswerId = answer.id,
                isAnswerCorrect = answer.isCorrect,
                showFeedback = true
            )
            
            // Cevabı kaydet
            val progress = submitAnswerUseCase(
                categoryId = category.id,
                question = question,
                answer = answer
            )
            
            // Skoru güncelle
            _uiState.value = _uiState.value.copy(
                score = progress.totalCorrect,
                totalAnswered = progress.totalAnswered,
                difficulty = progress.currentDifficulty
            )
            
            // Kısa bir gecikme sonrası bir sonraki soruya geç
            delay(1500)
            loadNextQuestion()
        }
    }
    
    fun retry() {
        loadNextQuestion()
    }
}

