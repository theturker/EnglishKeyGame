package com.alperenturker.englishcardgame.core.domain.repository

import com.alperenturker.englishcardgame.core.domain.model.UserProgress

interface UserProgressRepository {
    suspend fun getProgress(categoryId: String): UserProgress?
    suspend fun saveProgress(progress: UserProgress)
    suspend fun getAllProgress(): List<UserProgress>
}

