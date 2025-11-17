package com.alperenturker.englishcardgame.core.domain.model

data class LevelTestQuestion(
    val id: String,
    val text: String,
    val options: List<AnswerOption>,
    val correctAnswerId: String
)

