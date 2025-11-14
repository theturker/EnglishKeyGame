package com.alperenturker.englishcardgame.feature.quiz.di

import com.alperenturker.englishcardgame.core.data.di.AppModule as DataAppModule
import com.alperenturker.englishcardgame.core.domain.usecase.GetCategoriesUseCase
import com.alperenturker.englishcardgame.core.domain.usecase.GetNextQuestionUseCase
import com.alperenturker.englishcardgame.core.domain.usecase.GetUserProgressUseCase
import com.alperenturker.englishcardgame.core.domain.usecase.SubmitAnswerUseCase
import com.alperenturker.englishcardgame.feature.quiz.viewmodel.CategoryListViewModel
import com.alperenturker.englishcardgame.feature.quiz.viewmodel.QuizViewModel

object AppModule {
    // Use Cases
    val getCategoriesUseCase: GetCategoriesUseCase by lazy {
        GetCategoriesUseCase(DataAppModule.questionRepository)
    }
    
    fun getNextQuestionUseCase(): GetNextQuestionUseCase {
        return GetNextQuestionUseCase(DataAppModule.questionRepository, DataAppModule.userProgressRepository)
    }
    
    fun submitAnswerUseCase(): SubmitAnswerUseCase {
        return SubmitAnswerUseCase(DataAppModule.userProgressRepository)
    }
    
    fun getUserProgressUseCase(): GetUserProgressUseCase {
        return GetUserProgressUseCase(DataAppModule.userProgressRepository)
    }
    
    // ViewModels
    fun categoryListViewModel(): CategoryListViewModel {
        return CategoryListViewModel(getCategoriesUseCase)
    }
    
    fun quizViewModel(
        categoryId: String,
        categoryName: String,
        categoryIcon: String?
    ): QuizViewModel {
        return QuizViewModel(
            getNextQuestionUseCase = getNextQuestionUseCase(),
            submitAnswerUseCase = submitAnswerUseCase(),
            getUserProgressUseCase = getUserProgressUseCase(),
            categoryId = categoryId,
            categoryName = categoryName,
            categoryIcon = categoryIcon
        )
    }
}

