package com.alperenturker.englishcardgame.core.domain.usecase

import com.alperenturker.englishcardgame.core.domain.model.UserProgress
import com.alperenturker.englishcardgame.core.domain.repository.UserProgressRepository

class GetUserProgressUseCase(
    private val userProgressRepository: UserProgressRepository
) {
    suspend operator fun invoke(categoryId: String): UserProgress? {
        return userProgressRepository.getProgress(categoryId)
    }
    
    suspend fun getAll(): List<UserProgress> {
        return userProgressRepository.getAllProgress()
    }
}

