package com.alperenturker.englishcardgame.feature.quiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperenturker.englishcardgame.core.domain.model.Category
import com.alperenturker.englishcardgame.core.domain.usecase.GetCategoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CategoryListUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val errorMessage: String? = null
)

class CategoryListViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CategoryListUiState())
    val uiState: StateFlow<CategoryListUiState> = _uiState.asStateFlow()
    
    init {
        loadCategories()
    }
    
    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val categories = getCategoriesUseCase()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    categories = categories
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Kategoriler yüklenirken bir hata oluştu"
                )
            }
        }
    }
    
    fun retry() {
        loadCategories()
    }
}

