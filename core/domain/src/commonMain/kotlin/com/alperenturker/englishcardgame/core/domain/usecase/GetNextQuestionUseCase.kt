package com.alperenturker.englishcardgame.core.domain.usecase

import com.alperenturker.englishcardgame.core.domain.model.Difficulty
import com.alperenturker.englishcardgame.core.domain.model.Question
import com.alperenturker.englishcardgame.core.domain.repository.QuestionRepository
import com.alperenturker.englishcardgame.core.domain.repository.UserProgressRepository

class GetNextQuestionUseCase(
    private val questionRepository: QuestionRepository,
    private val userProgressRepository: UserProgressRepository
) {
    suspend operator fun invoke(categoryId: String): Question {
        val progress = userProgressRepository.getProgress(categoryId)
        val difficulty = progress?.currentDifficulty ?: Difficulty.EASY
        return questionRepository.getNextQuestion(categoryId, difficulty)
    }
}

