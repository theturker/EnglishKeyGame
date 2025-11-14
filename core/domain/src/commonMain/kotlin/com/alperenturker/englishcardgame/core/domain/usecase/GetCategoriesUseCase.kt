package com.alperenturker.englishcardgame.core.domain.usecase

import com.alperenturker.englishcardgame.core.domain.model.Category
import com.alperenturker.englishcardgame.core.domain.repository.QuestionRepository

class GetCategoriesUseCase(
    private val questionRepository: QuestionRepository
) {
    suspend operator fun invoke(): List<Category> {
        return questionRepository.getCategories()
    }
}

