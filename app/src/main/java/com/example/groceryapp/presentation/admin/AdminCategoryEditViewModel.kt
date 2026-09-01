package com.example.groceryapp.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceryapp.data.repository.AdminRepository
import com.example.groceryapp.data.repository.CategoryRepository
import com.example.groceryapp.domain.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AdminCategoryEditState {
    object Idle : AdminCategoryEditState()
    object Loading : AdminCategoryEditState()
    object Success : AdminCategoryEditState()
    data class Error(val message: String) : AdminCategoryEditState()
}

class AdminCategoryEditViewModel(
    private val adminRepository: AdminRepository = AdminRepository(),
    private val categoryRepository: CategoryRepository = CategoryRepository()
) : ViewModel() {
    private val _state = MutableStateFlow<AdminCategoryEditState>(AdminCategoryEditState.Idle)
    val state: StateFlow<AdminCategoryEditState> = _state.asStateFlow()

    private val _category = MutableStateFlow<Category?>(null)
    val category: StateFlow<Category?> = _category.asStateFlow()

    fun loadCategory(id: String) {
        if (id == "new") {
            _category.value = null
            return
        }
        viewModelScope.launch {
            _state.value = AdminCategoryEditState.Loading
            categoryRepository.getCategories().collect { result ->
                result.onSuccess { list ->
                    val found = list.find { it.id == id }
                    if (found != null) {
                        _category.value = found
                        _state.value = AdminCategoryEditState.Idle
                    } else {
                        _state.value = AdminCategoryEditState.Error("Category not found")
                    }
                }.onFailure {
                    _state.value = AdminCategoryEditState.Error("Failed to fetch categories")
                }
            }
        }
    }

    fun saveCategory(
        id: String?,
        name: String,
        icon: String,
        imageUrl: String,
        displayOrder: Int
    ) {
        viewModelScope.launch {
            _state.value = AdminCategoryEditState.Loading
            val categoryToSave = Category(
                id = id ?: "",
                name = name,
                icon = icon,
                imageUrl = imageUrl,
                displayOrder = displayOrder
            )

            val result = if (id == null) {
                adminRepository.createCategory(categoryToSave)
            } else {
                adminRepository.updateCategory(id, categoryToSave)
            }

            result.onSuccess {
                _state.value = AdminCategoryEditState.Success
            }.onFailure {
                _state.value = AdminCategoryEditState.Error(it.message ?: "Failed to save category")
            }
        }
    }
}
