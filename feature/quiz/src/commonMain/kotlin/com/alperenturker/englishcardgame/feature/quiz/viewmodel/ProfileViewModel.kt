package com.alperenturker.englishcardgame.feature.quiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperenturker.englishcardgame.core.data.di.AppModule as DataAppModule
import com.alperenturker.englishcardgame.core.domain.model.Difficulty
import com.alperenturker.englishcardgame.core.domain.model.UserProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val currentLevel: Difficulty = Difficulty.EASY,
    val totalAnswered: Int = 0,
    val totalCorrect: Int = 0,
    val accuracy: Float = 0f,
    val categoryProgress: List<UserProgress> = emptyList(),
    val isLoading: Boolean = true
)

class ProfileViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    private val userProgressRepository = DataAppModule.userProgressRepository
    private val appSettings = DataAppModule.appSettings
    
    init {
        loadProfileData()
    }
    
    private fun loadProfileData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Get initial difficulty from settings
                val initialDifficultyStr = appSettings.getInitialDifficulty()
                val initialDifficulty = try {
                    Difficulty.valueOf(initialDifficultyStr ?: "EASY")
                } catch (e: Exception) {
                    Difficulty.EASY
                }
                
                // Get all category progress
                val allProgress = userProgressRepository.getAllProgress()
                
                // Calculate totals
                val totalAnswered = allProgress.sumOf { it.totalAnswered }
                val totalCorrect = allProgress.sumOf { it.totalCorrect }
                val accuracy = if (totalAnswered > 0) {
                    totalCorrect.toFloat() / totalAnswered
                } else 0f
                
                // Determine current level (highest difficulty or initial)
                val currentLevel = allProgress.maxByOrNull { 
                    when (it.currentDifficulty) {
                        Difficulty.EASY -> 1
                        Difficulty.MEDIUM -> 2
                        Difficulty.HARD -> 3
                    }
                }?.currentDifficulty ?: initialDifficulty
                
                _uiState.value = ProfileUiState(
                    currentLevel = currentLevel,
                    totalAnswered = totalAnswered,
                    totalCorrect = totalCorrect,
                    accuracy = accuracy,
                    categoryProgress = allProgress,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
    
    fun refresh() {
        loadProfileData()
    }
}

