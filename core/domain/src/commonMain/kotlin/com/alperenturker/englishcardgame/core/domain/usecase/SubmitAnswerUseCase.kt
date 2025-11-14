package com.alperenturker.englishcardgame.core.domain.usecase

import com.alperenturker.englishcardgame.core.domain.model.AnswerOption
import com.alperenturker.englishcardgame.core.domain.model.Difficulty
import com.alperenturker.englishcardgame.core.domain.model.Question
import com.alperenturker.englishcardgame.core.domain.model.UserProgress
import com.alperenturker.englishcardgame.core.domain.model.updateWithAnswer
import com.alperenturker.englishcardgame.core.domain.repository.UserProgressRepository

class SubmitAnswerUseCase(
    private val userProgressRepository: UserProgressRepository
) {
    suspend operator fun invoke(
        categoryId: String,
        question: Question,
        answer: AnswerOption
    ): UserProgress {
        val wasCorrect = answer.isCorrect
        
        val current = userProgressRepository.getProgress(categoryId) ?: UserProgress(
            categoryId = categoryId,
            totalAnswered = 0,
            totalCorrect = 0,
            currentDifficulty = question.difficulty,
            recentAnswers = emptyList()
        )
        
        val updated = current.updateWithAnswer(wasCorrect)
        userProgressRepository.saveProgress(updated)
        
        return updated
    }
}

