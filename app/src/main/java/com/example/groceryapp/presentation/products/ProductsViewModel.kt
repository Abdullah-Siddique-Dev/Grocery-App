package com.example.groceryapp.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceryapp.data.repository.ProductRepository
import com.example.groceryapp.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProductsState {
    object Loading : ProductsState()
    data class Success(val products: List<Product>) : ProductsState()
    data class Error(val message: String) : ProductsState()
    object Empty : ProductsState()
}

class ProductsViewModel(private val repository: ProductRepository = ProductRepository()) : ViewModel() {
    private val _state = MutableStateFlow<ProductsState>(ProductsState.Loading)
    val state: StateFlow<ProductsState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun loadProducts(categoryId: String? = null, query: String? = null) {
        viewModelScope.launch {
            _state.value = ProductsState.Loading
            repository.getProducts(categoryId, query).collect { result ->
                result.onSuccess { list ->
                    _state.value = if (list.isEmpty()) ProductsState.Empty else ProductsState.Success(list)
                }.onFailure {
                    _state.value = ProductsState.Error(it.message ?: "Unknown error")
                }
            }
        }
    }

    fun onSearchQueryChange(newQuery: String, categoryId: String? = null) {
        _searchQuery.value = newQuery
        loadProducts(categoryId, newQuery)
    }
}
