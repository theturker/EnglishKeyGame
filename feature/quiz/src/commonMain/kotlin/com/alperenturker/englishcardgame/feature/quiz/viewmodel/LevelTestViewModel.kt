package com.alperenturker.englishcardgame.feature.quiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperenturker.englishcardgame.core.data.di.AppModule
import com.alperenturker.englishcardgame.core.domain.model.Difficulty
import com.alperenturker.englishcardgame.core.domain.model.LevelTestQuestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LevelTestUiState(
    val questions: List<LevelTestQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswerId: String? = null,
    val score: Int = 0,
    val showResult: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class LevelTestViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(LevelTestUiState())
    val uiState: StateFlow<LevelTestUiState> = _uiState.asStateFlow()
    
    private val levelTestRepository = AppModule.levelTestRepository
    private val appSettings = AppModule.appSettings
    
    init {
        loadQuestions()
    }
    
    private fun loadQuestions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val questions = levelTestRepository.getLevelTestQuestions()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    questions = questions
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Sorular yüklenirken bir hata oluştu"
                )
            }
        }
    }
    
    fun onAnswerSelected(answerId: String) {
        val currentState = _uiState.value
        val currentQuestion = currentState.questions.getOrNull(currentState.currentQuestionIndex)
        
        if (currentQuestion != null && currentState.selectedAnswerId == null) {
            val isCorrect = answerId == currentQuestion.correctAnswerId
            val newScore = if (isCorrect) currentState.score + 1 else currentState.score
            
            _uiState.value = currentState.copy(
                selectedAnswerId = answerId,
                score = newScore
            )
        }
    }
    
    fun onNextQuestion() {
        val currentState = _uiState.value
        if (currentState.currentQuestionIndex < currentState.questions.size - 1) {
            _uiState.value = currentState.copy(
                currentQuestionIndex = currentState.currentQuestionIndex + 1,
                selectedAnswerId = null
            )
        }
    }
    
    fun completeTest() {
        val currentState = _uiState.value
        val score = currentState.score
        val totalQuestions = currentState.questions.size
        
        viewModelScope.launch {
            try {
                // Calculate difficulty based on score
                val difficulty = levelTestRepository.calculateDifficulty(score)
                
                // Save level test completion and initial difficulty
                appSettings.setLevelTestCompleted(true)
                appSettings.setInitialDifficulty(difficulty.name)
                
                _uiState.value = currentState.copy(showResult = true)
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    errorMessage = e.message ?: "Test tamamlanırken bir hata oluştu"
                )
            }
        }
    }
    
    fun retry() {
        loadQuestions()
    }
}

