package com.alperenturker.englishcardgame.core.domain.repository

import com.alperenturker.englishcardgame.core.domain.model.LevelTestQuestion

interface LevelTestRepository {
    suspend fun getLevelTestQuestions(): List<LevelTestQuestion>
    suspend fun calculateDifficulty(score: Int): com.alperenturker.englishcardgame.core.domain.model.Difficulty
}

