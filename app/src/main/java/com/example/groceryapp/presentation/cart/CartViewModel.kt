package com.example.groceryapp.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceryapp.data.repository.CartRepository
import com.example.groceryapp.data.repository.ProductRepository
import com.example.groceryapp.domain.model.CartItem
import com.example.groceryapp.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CartItemUiState(
    val productId: String,
    val productName: String,
    val productImageUrl: String,
    val quantity: Int,
    val price: Double,
    val subtotal: Double,
    val maxStock: Int
)

sealed class CartState {
    object Loading : CartState()
    data class Success(
        val items: List<CartItemUiState>,
        val total: Double
    ) : CartState()
    object Empty : CartState()
    data class Error(val message: String) : CartState()
}

class CartViewModel(
    private val cartRepository: CartRepository = CartRepository(),
    private val productRepository: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<CartState>(CartState.Loading)
    val state: StateFlow<CartState> = _state.asStateFlow()

    init {
        loadCart()
    }

    fun loadCart() {
        viewModelScope.launch {
            _state.value = CartState.Loading
            cartRepository.getCart().collect { result ->
                result.onSuccess { cart ->
                    if (cart.items.isEmpty()) {
                        _state.value = CartState.Empty
                    } else {
                        // In a real app, we might get product details in the cart response
                        // or fetch them in bulk. Here we'll simulate the mapping.
                        val uiItems = cart.items.map { item ->
                            CartItemUiState(
                                productId = item.productId,
                                productName = item.productName,
                                productImageUrl = item.productImageUrl,
                                quantity = item.quantity,
                                price = item.price,
                                subtotal = item.subtotal,
                                maxStock = item.stockQuantity
                            )
                        }
                        _state.value = CartState.Success(uiItems, cart.total)
                    }
                }.onFailure {
                    _state.value = CartState.Error(it.message ?: "Failed to load cart")
                }
            }
        }
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            cartRepository.addItem(product.id, quantity, product.price).onSuccess {
                loadCart()
            }
        }
    }

    fun updateQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            removeItem(productId)
            return
        }
        viewModelScope.launch {
            cartRepository.updateQuantity(productId, quantity).onSuccess {
                loadCart()
            }
        }
    }

    fun removeItem(productId: String) {
        viewModelScope.launch {
            cartRepository.removeItem(productId).onSuccess {
                loadCart()
            }
        }
    }
}
