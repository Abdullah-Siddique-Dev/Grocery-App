package com.example.groceryapp.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceryapp.data.repository.CartRepository
import com.example.groceryapp.data.repository.ProductRepository
import com.example.groceryapp.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProductDetailsState {
    object Loading : ProductDetailsState()
    data class Success(val product: Product) : ProductDetailsState()
    data class Error(val message: String) : ProductDetailsState()
}

class ProductDetailsViewModel(
    private val repository: ProductRepository = ProductRepository(),
    private val cartRepository: CartRepository = CartRepository()
) : ViewModel() {
    private val _state = MutableStateFlow<ProductDetailsState>(ProductDetailsState.Loading)
    val state: StateFlow<ProductDetailsState> = _state.asStateFlow()

    private val _isAddedToCart = MutableStateFlow(false)
    val isAddedToCart: StateFlow<Boolean> = _isAddedToCart.asStateFlow()

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _state.value = ProductDetailsState.Loading
            repository.getProductById(productId).collect { result ->
                result.onSuccess { product ->
                    _state.value = ProductDetailsState.Success(product)
                }.onFailure {
                    _state.value = ProductDetailsState.Error(it.message ?: "Unknown error")
                }
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            cartRepository.addItem(product.id, 1, product.price).onSuccess {
                _isAddedToCart.value = true
            }
        }
    }

    fun resetAddedToCart() {
        _isAddedToCart.value = false
    }
}
