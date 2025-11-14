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
    val currentQuestionNumber: Int = 0,
    val totalQuestions: Int = 10,
    val difficulty: Difficulty = Difficulty.EASY,
    val errorMessage: String? = null,
    val isQuizCompleted: Boolean = false,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0,
    val previousDifficulty: Difficulty? = null
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
    
    private val _uiState = MutableStateFlow(
        QuizUiState(
            category = category,
            currentQuestionNumber = 0,
            totalQuestions = 10
        )
    )
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()
    
    private var correctCount = 0
    private var wrongCount = 0
    private var startDifficulty: Difficulty? = null
    
    init {
        // Quiz başladığında soru tracking'ini sıfırla
        com.alperenturker.englishcardgame.core.data.di.AppModule.resetQuestionTracking(
            categoryId = category.id,
            difficulty = Difficulty.EASY // İlk başta EASY, sonra progress'e göre güncellenir
        )
        
        loadInitialProgress()
        loadNextQuestion()
    }
    
    private fun loadInitialProgress() {
        viewModelScope.launch {
            val progress = getUserProgressUseCase(category.id)
            progress?.let {
                startDifficulty = it.currentDifficulty
                _uiState.value = _uiState.value.copy(
                    score = it.totalCorrect,
                    totalAnswered = it.totalAnswered,
                    difficulty = it.currentDifficulty,
                    previousDifficulty = it.currentDifficulty
                )
            }
        }
    }
    
    fun loadNextQuestion() {
        viewModelScope.launch {
            // Quiz tamamlandı mı kontrol et
            if (_uiState.value.currentQuestionNumber >= _uiState.value.totalQuestions) {
                completeQuiz()
                return@launch
            }
            
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
                    difficulty = question.difficulty,
                    currentQuestionNumber = _uiState.value.currentQuestionNumber + 1
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
        val currentState = _uiState.value
        
        if (currentState.selectedAnswerId != null || currentState.isLoading || currentState.isQuizCompleted) {
            return // Zaten bir cevap seçildi veya yükleniyor veya quiz tamamlandı
        }
        
        val question = currentState.currentQuestion ?: return
        
        viewModelScope.launch {
            val isCorrect = answer.isCorrect
            if (isCorrect) {
                correctCount++
            } else {
                wrongCount++
            }
            
            // Önce feedback'i göster
            _uiState.value = _uiState.value.copy(
                selectedAnswerId = answer.id,
                isAnswerCorrect = isCorrect,
                showFeedback = true
            )
            
            // Cevabı kaydet
            val progress = submitAnswerUseCase(
                categoryId = category.id,
                question = question,
                answer = answer
            )
            
            // Skoru güncelle (showFeedback'i koruyarak)
            _uiState.value = _uiState.value.copy(
                score = progress.totalCorrect,
                totalAnswered = progress.totalAnswered,
                difficulty = progress.currentDifficulty,
                correctAnswers = correctCount,
                wrongAnswers = wrongCount,
                showFeedback = true, // showFeedback'i koru
                selectedAnswerId = answer.id, // selectedAnswerId'yi koru
                isAnswerCorrect = isCorrect // isAnswerCorrect'i koru
            )
            
            // Kısa bir gecikme sonrası bir sonraki soruya geç veya quiz'i tamamla
            delay(2000)
            
            // Son soru mu kontrol et (state'i tekrar oku)
            val finalState = _uiState.value
            if (finalState.currentQuestionNumber >= finalState.totalQuestions) {
                completeQuiz()
            } else {
                loadNextQuestion()
            }
        }
    }
    
    private fun completeQuiz() {
        viewModelScope.launch {
            val previousDiff = startDifficulty ?: _uiState.value.previousDifficulty ?: Difficulty.EASY
            _uiState.value = _uiState.value.copy(
                isQuizCompleted = true,
                correctAnswers = correctCount,
                wrongAnswers = wrongCount,
                previousDifficulty = previousDiff
            )
        }
    }
    
    fun restartQuiz() {
        correctCount = 0
        wrongCount = 0
        
        // Soru tracking'ini sıfırla (repository'de)
        com.alperenturker.englishcardgame.core.data.di.AppModule.resetQuestionTracking(
            categoryId = category.id,
            difficulty = _uiState.value.difficulty
        )
        
        _uiState.value = _uiState.value.copy(
            isQuizCompleted = false,
            currentQuestionNumber = 0,
            correctAnswers = 0,
            wrongAnswers = 0,
            selectedAnswerId = null,
            isAnswerCorrect = null,
            showFeedback = false
        )
        loadNextQuestion()
    }
    
    fun retry() {
        loadNextQuestion()
    }
    
    fun finishQuiz(onFinish: () -> Unit) {
        onFinish()
    }
}

