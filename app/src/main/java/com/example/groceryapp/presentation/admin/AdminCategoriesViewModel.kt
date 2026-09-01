package com.example.groceryapp.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceryapp.data.repository.AdminRepository
import com.example.groceryapp.domain.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AdminCategoriesState {
    object Loading : AdminCategoriesState()
    data class Success(val categories: List<Category>) : AdminCategoriesState()
    data class Error(val message: String) : AdminCategoriesState()
}

class AdminCategoriesViewModel(private val repository: AdminRepository = AdminRepository()) : ViewModel() {
    private val _state = MutableStateFlow<AdminCategoriesState>(AdminCategoriesState.Loading)
    val state: StateFlow<AdminCategoriesState> = _state.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _state.value = AdminCategoriesState.Loading
            repository.getAllCategories().collect { result ->
                result.onSuccess {
                    _state.value = AdminCategoriesState.Success(it)
                }.onFailure {
                    _state.value = AdminCategoriesState.Error(it.message ?: "Failed to load categories")
                }
            }
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            repository.deleteCategory(id).onSuccess {
                loadCategories()
            }
        }
    }
}
