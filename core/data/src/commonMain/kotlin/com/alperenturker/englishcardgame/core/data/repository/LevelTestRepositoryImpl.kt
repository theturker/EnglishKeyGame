package com.alperenturker.englishcardgame.core.data.repository

import com.alperenturker.englishcardgame.core.domain.model.AnswerOption
import com.alperenturker.englishcardgame.core.domain.model.Difficulty
import com.alperenturker.englishcardgame.core.domain.model.LevelTestQuestion
import com.alperenturker.englishcardgame.core.domain.repository.LevelTestRepository

class LevelTestRepositoryImpl : LevelTestRepository {
    
    override suspend fun getLevelTestQuestions(): List<LevelTestQuestion> {
        // 10 sabit soru - herkese aynı sorular sorulacak
        return listOf(
            LevelTestQuestion(
                id = "lt_1",
                text = "Choose the correct form: I _____ to the store yesterday.",
                options = listOf(
                    AnswerOption(id = "a", text = "go", isCorrect = false),
                    AnswerOption(id = "b", text = "went", isCorrect = true),
                    AnswerOption(id = "c", text = "goed", isCorrect = false),
                    AnswerOption(id = "d", text = "going", isCorrect = false)
                ),
                correctAnswerId = "b"
            ),
            LevelTestQuestion(
                id = "lt_2",
                text = "What is the past tense of 'eat'?",
                options = listOf(
                    AnswerOption(id = "a", text = "eated", isCorrect = false),
                    AnswerOption(id = "b", text = "ate", isCorrect = true),
                    AnswerOption(id = "c", text = "eaten", isCorrect = false),
                    AnswerOption(id = "d", text = "eating", isCorrect = false)
                ),
                correctAnswerId = "b"
            ),
            LevelTestQuestion(
                id = "lt_3",
                text = "She _____ English for five years.",
                options = listOf(
                    AnswerOption(id = "a", text = "has been studying", isCorrect = true),
                    AnswerOption(id = "b", text = "is studying", isCorrect = false),
                    AnswerOption(id = "c", text = "studies", isCorrect = false),
                    AnswerOption(id = "d", text = "studied", isCorrect = false)
                ),
                correctAnswerId = "a"
            ),
            LevelTestQuestion(
                id = "lt_4",
                text = "If I _____ rich, I would travel the world.",
                options = listOf(
                    AnswerOption(id = "a", text = "am", isCorrect = false),
                    AnswerOption(id = "b", text = "was", isCorrect = false),
                    AnswerOption(id = "c", text = "were", isCorrect = true),
                    AnswerOption(id = "d", text = "be", isCorrect = false)
                ),
                correctAnswerId = "c"
            ),
            LevelTestQuestion(
                id = "lt_5",
                text = "The book _____ by millions of people.",
                options = listOf(
                    AnswerOption(id = "a", text = "reads", isCorrect = false),
                    AnswerOption(id = "b", text = "is read", isCorrect = true),
                    AnswerOption(id = "c", text = "read", isCorrect = false),
                    AnswerOption(id = "d", text = "reading", isCorrect = false)
                ),
                correctAnswerId = "b"
            ),
            LevelTestQuestion(
                id = "lt_6",
                text = "I wish I _____ harder when I was younger.",
                options = listOf(
                    AnswerOption(id = "a", text = "study", isCorrect = false),
                    AnswerOption(id = "b", text = "studied", isCorrect = true),
                    AnswerOption(id = "c", text = "had studied", isCorrect = false),
                    AnswerOption(id = "d", text = "would study", isCorrect = false)
                ),
                correctAnswerId = "b"
            ),
            LevelTestQuestion(
                id = "lt_7",
                text = "By next year, I _____ here for ten years.",
                options = listOf(
                    AnswerOption(id = "a", text = "will work", isCorrect = false),
                    AnswerOption(id = "b", text = "will have worked", isCorrect = true),
                    AnswerOption(id = "c", text = "work", isCorrect = false),
                    AnswerOption(id = "d", text = "am working", isCorrect = false)
                ),
                correctAnswerId = "b"
            ),
            LevelTestQuestion(
                id = "lt_8",
                text = "He suggested that she _____ a doctor.",
                options = listOf(
                    AnswerOption(id = "a", text = "see", isCorrect = true),
                    AnswerOption(id = "b", text = "sees", isCorrect = false),
                    AnswerOption(id = "c", text = "saw", isCorrect = false),
                    AnswerOption(id = "d", text = "seeing", isCorrect = false)
                ),
                correctAnswerId = "a"
            ),
            LevelTestQuestion(
                id = "lt_9",
                text = "Not only _____ late, but he also forgot his homework.",
                options = listOf(
                    AnswerOption(id = "a", text = "he was", isCorrect = false),
                    AnswerOption(id = "b", text = "was he", isCorrect = true),
                    AnswerOption(id = "c", text = "he is", isCorrect = false),
                    AnswerOption(id = "d", text = "is he", isCorrect = false)
                ),
                correctAnswerId = "b"
            ),
            LevelTestQuestion(
                id = "lt_10",
                text = "The more you practice, _____ you become.",
                options = listOf(
                    AnswerOption(id = "a", text = "the better", isCorrect = true),
                    AnswerOption(id = "b", text = "better", isCorrect = false),
                    AnswerOption(id = "c", text = "the best", isCorrect = false),
                    AnswerOption(id = "d", text = "best", isCorrect = false)
                ),
                correctAnswerId = "a"
            )
        )
    }
    
    override suspend fun calculateDifficulty(score: Int): Difficulty {
        // 0-3: EASY, 4-6: MEDIUM, 7-10: HARD
        return when {
            score <= 3 -> Difficulty.EASY
            score <= 6 -> Difficulty.MEDIUM
            else -> Difficulty.HARD
        }
    }
}

