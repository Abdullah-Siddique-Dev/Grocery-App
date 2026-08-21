package com.example.groceryapp.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceryapp.data.repository.CategoryRepository
import com.example.groceryapp.domain.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CategoriesState {
    object Loading : CategoriesState()
    data class Success(val categories: List<Category>) : CategoriesState()
    data class Error(val message: String) : CategoriesState()
    object Empty : CategoriesState()
}

class CategoriesViewModel(private val repository: CategoryRepository = CategoryRepository()) : ViewModel() {
    private val _state = MutableStateFlow<CategoriesState>(CategoriesState.Loading)
    val state: StateFlow<CategoriesState> = _state.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _state.value = CategoriesState.Loading
            repository.getCategories().collect { result ->
                result.onSuccess { list ->
                    _state.value = if (list.isEmpty()) CategoriesState.Empty else CategoriesState.Success(list)
                }.onFailure {
                    _state.value = CategoriesState.Error(it.message ?: "Unknown error")
                }
            }
        }
    }
}
