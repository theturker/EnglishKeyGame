package com.alperenturker.englishcardgame.core.domain.model

data class Question(
    val id: String,
    val categoryId: String,
    val text: String,
    val options: List<AnswerOption>,
    val difficulty: Difficulty
)

