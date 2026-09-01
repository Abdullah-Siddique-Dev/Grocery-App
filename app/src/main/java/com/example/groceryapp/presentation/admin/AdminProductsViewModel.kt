package com.example.groceryapp.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceryapp.data.repository.AdminRepository
import com.example.groceryapp.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AdminProductsState {
    object Loading : AdminProductsState()
    data class Success(val products: List<Product>) : AdminProductsState()
    data class Error(val message: String) : AdminProductsState()
}

class AdminProductsViewModel(private val repository: AdminRepository = AdminRepository()) : ViewModel() {
    private val _state = MutableStateFlow<AdminProductsState>(AdminProductsState.Loading)
    val state: StateFlow<AdminProductsState> = _state.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _state.value = AdminProductsState.Loading
            repository.getAllProducts().collect { result ->
                result.onSuccess {
                    _state.value = AdminProductsState.Success(it)
                }.onFailure {
                    _state.value = AdminProductsState.Error(it.message ?: "Failed to load products")
                }
            }
        }
    }

    fun deleteProduct(id: String) {
        viewModelScope.launch {
            repository.deleteProduct(id).onSuccess {
                loadProducts()
            }
        }
    }
}
