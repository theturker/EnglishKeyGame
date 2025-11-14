package com.alperenturker.englishcardgame.core.data.mapper

import com.alperenturker.englishcardgame.core.domain.model.UserProgress
import kotlinx.serialization.Serializable

@Serializable
data class UserProgressSerializable(
    val categoryId: String,
    val totalAnswered: Int,
    val totalCorrect: Int,
    val currentDifficulty: String,
    val recentAnswers: List<Boolean>
)

fun UserProgress.toSerializable(): UserProgressSerializable {
    return UserProgressSerializable(
        categoryId = categoryId,
        totalAnswered = totalAnswered,
        totalCorrect = totalCorrect,
        currentDifficulty = currentDifficulty.name,
        recentAnswers = recentAnswers
    )
}

fun UserProgressSerializable.toDomain(): UserProgress {
    val difficulty = try {
        com.alperenturker.englishcardgame.core.domain.model.Difficulty.valueOf(currentDifficulty)
    } catch (e: Exception) {
        com.alperenturker.englishcardgame.core.domain.model.Difficulty.EASY
    }
    
    return UserProgress(
        categoryId = categoryId,
        totalAnswered = totalAnswered,
        totalCorrect = totalCorrect,
        currentDifficulty = difficulty,
        recentAnswers = recentAnswers
    )
}

