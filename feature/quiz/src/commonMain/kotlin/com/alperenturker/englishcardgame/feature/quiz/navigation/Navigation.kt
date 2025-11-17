package com.alperenturker.englishcardgame.feature.quiz.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.alperenturker.englishcardgame.core.data.di.AppModule as DataAppModule
import com.alperenturker.englishcardgame.feature.quiz.di.AppModule
import com.alperenturker.englishcardgame.feature.quiz.ui.CategoryListScreen
import com.alperenturker.englishcardgame.feature.quiz.ui.LevelTestScreen
import com.alperenturker.englishcardgame.feature.quiz.ui.OnboardingScreen
import com.alperenturker.englishcardgame.feature.quiz.ui.ProfileScreen
import com.alperenturker.englishcardgame.feature.quiz.ui.QuizScreen
import com.alperenturker.englishcardgame.feature.quiz.viewmodel.CategoryListViewModel
import kotlinx.coroutines.launch

sealed class Screen {
    object Onboarding : Screen()
    object LevelTest : Screen()
    object CategoryList : Screen()
    object Profile : Screen()
    data class Quiz(val categoryId: String, val categoryName: String, val categoryIcon: String?) : Screen()
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    var currentScreen by remember { mutableStateOf<Screen?>(null) }
    val scope = rememberCoroutineScope()
    
    // Check onboarding and level test status
    LaunchedEffect(Unit) {
        val hasCompletedOnboarding = DataAppModule.appSettings.hasCompletedOnboarding()
        val hasCompletedLevelTest = DataAppModule.appSettings.hasCompletedLevelTest()
        
        currentScreen = when {
            !hasCompletedOnboarding -> Screen.Onboarding
            !hasCompletedLevelTest -> Screen.LevelTest
            else -> Screen.CategoryList
        }
    }
    
    // Create ViewModel using remember for cross-platform compatibility
    val categoryListViewModel = remember { AppModule.categoryListViewModel() }
    val levelTestViewModel = remember { AppModule.levelTestViewModel() }
    val profileViewModel = remember { AppModule.profileViewModel() }
    
    Box(modifier = modifier) {
        when (val screen = currentScreen) {
            is Screen.Onboarding -> {
                OnboardingScreen(
                    onComplete = {
                        scope.launch {
                            DataAppModule.appSettings.setOnboardingCompleted(true)
                            currentScreen = Screen.LevelTest
                        }
                    }
                )
            }
            
            is Screen.LevelTest -> {
                val uiState by levelTestViewModel.uiState.collectAsState()
                
                LevelTestScreen(
                    questions = uiState.questions,
                    currentQuestionIndex = uiState.currentQuestionIndex,
                    selectedAnswerId = uiState.selectedAnswerId,
                    showResult = uiState.showResult,
                    score = uiState.score,
                    onAnswerSelected = { answerId ->
                        levelTestViewModel.onAnswerSelected(answerId)
                    },
                    onNextQuestion = {
                        levelTestViewModel.onNextQuestion()
                    },
                    onComplete = {
                        levelTestViewModel.completeTest()
                    },
                    onResultComplete = {
                        scope.launch {
                            currentScreen = Screen.CategoryList
                        }
                    }
                )
            }
            
            is Screen.CategoryList -> {
                CategoryListScreen(
                    onCategorySelected = { categoryId, categoryName, categoryIcon ->
                        currentScreen = Screen.Quiz(categoryId, categoryName, categoryIcon)
                    },
                    onProfileClick = {
                        currentScreen = Screen.Profile
                    },
                    viewModel = categoryListViewModel
                )
            }
            
            is Screen.Profile -> {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onBackClick = {
                        currentScreen = Screen.CategoryList
                    }
                )
            }
            
            is Screen.Quiz -> {
                // Create QuizViewModel with screen parameters using key to recreate on screen change
                val quizViewModel = remember(screen.categoryId) {
                    AppModule.quizViewModel(
                        categoryId = screen.categoryId,
                        categoryName = screen.categoryName,
                        categoryIcon = screen.categoryIcon
                    )
                }
                
                QuizScreen(
                    categoryId = screen.categoryId,
                    categoryName = screen.categoryName,
                    categoryIcon = screen.categoryIcon,
                    onBackClick = {
                        currentScreen = Screen.CategoryList
                    },
                    viewModel = quizViewModel
                )
            }
            
            null -> {
                // Loading state
            }
        }
    }
}

