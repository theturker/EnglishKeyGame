package com.alperenturker.englishcardgame.core.data.repository

import com.alperenturker.englishcardgame.core.data.datasource.LocalUserProgressDataSource
import com.alperenturker.englishcardgame.core.domain.model.UserProgress
import com.alperenturker.englishcardgame.core.domain.repository.UserProgressRepository

class UserProgressRepositoryImpl(
    private val localUserProgressDataSource: LocalUserProgressDataSource
) : UserProgressRepository {
    
    override suspend fun getProgress(categoryId: String): UserProgress? {
        return localUserProgressDataSource.getProgress(categoryId)
    }
    
    override suspend fun saveProgress(progress: UserProgress) {
        localUserProgressDataSource.saveProgress(progress)
    }
    
    override suspend fun getAllProgress(): List<UserProgress> {
        return localUserProgressDataSource.getAllProgress()
    }
}

