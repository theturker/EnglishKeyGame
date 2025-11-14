package com.alperenturker.englishcardgame.core.domain.model

data class UserProgress(
    val categoryId: String,
    val totalAnswered: Int,
    val totalCorrect: Int,
    val currentDifficulty: Difficulty,
    val recentAnswers: List<Boolean> // Son N cevap için adaptif zorluk
) {
    val accuracy: Float
        get() = if (totalAnswered > 0) totalCorrect.toFloat() / totalAnswered else 0f
}

/**
 * Yeni bir cevap verildiğinde UserProgress'i günceller ve adaptif zorluk mantığını uygular
 */
fun UserProgress.updateWithAnswer(correct: Boolean): UserProgress {
    val updatedAnswers = (recentAnswers + correct).takeLast(3)
    
    val nextDifficulty = when {
        // Son 3 cevap doğru ise zorluk artır
        updatedAnswers.size == 3 && updatedAnswers.all { it } -> increaseDifficulty(currentDifficulty)
        // Son 3 cevap yanlış ise zorluk azalt
        updatedAnswers.size == 3 && updatedAnswers.all { !it } -> decreaseDifficulty(currentDifficulty)
        // Diğer durumlarda mevcut zorlukta kal
        else -> currentDifficulty
    }
    
    return copy(
        totalAnswered = totalAnswered + 1,
        totalCorrect = totalCorrect + if (correct) 1 else 0,
        currentDifficulty = nextDifficulty,
        recentAnswers = updatedAnswers
    )
}

/**
 * Zorluğu bir seviye artırır (EASY -> MEDIUM -> HARD)
 */
fun increaseDifficulty(d: Difficulty): Difficulty = when (d) {
    Difficulty.EASY -> Difficulty.MEDIUM
    Difficulty.MEDIUM -> Difficulty.HARD
    Difficulty.HARD -> Difficulty.HARD // Maksimum zorluk
}

/**
 * Zorluğu bir seviye azaltır (HARD -> MEDIUM -> EASY)
 */
fun decreaseDifficulty(d: Difficulty): Difficulty = when (d) {
    Difficulty.EASY -> Difficulty.EASY // Minimum zorluk
    Difficulty.MEDIUM -> Difficulty.EASY
    Difficulty.HARD -> Difficulty.MEDIUM
}

