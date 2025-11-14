package com.alperenturker.englishcardgame.feature.quiz.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alperenturker.englishcardgame.feature.quiz.di.AppModule
import com.alperenturker.englishcardgame.feature.quiz.ui.CategoryListScreen
import com.alperenturker.englishcardgame.feature.quiz.ui.QuizScreen
import com.alperenturker.englishcardgame.feature.quiz.viewmodel.CategoryListViewModel

sealed class Screen {
    object CategoryList : Screen()
    data class Quiz(val categoryId: String, val categoryName: String, val categoryIcon: String?) : Screen()
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.CategoryList) }
    
    Box(modifier = modifier) {
        when (val screen = currentScreen) {
        is Screen.CategoryList -> {
            CategoryListScreen(
                onCategorySelected = { categoryId, categoryName, categoryIcon ->
                    currentScreen = Screen.Quiz(categoryId, categoryName, categoryIcon)
                },
                viewModel = viewModel(
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            return AppModule.categoryListViewModel() as T
                        }
                    }
                )
            )
        }
        
        is Screen.Quiz -> {
            QuizScreen(
                categoryId = screen.categoryId,
                categoryName = screen.categoryName,
                categoryIcon = screen.categoryIcon,
                onBackClick = {
                    currentScreen = Screen.CategoryList
                }
            )
        }
        }
    }
}

