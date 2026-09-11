package com.example.groceryapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceryapp.data.repository.AuthRepository
import com.example.groceryapp.data.repository.CategoryRepository
import com.example.groceryapp.data.repository.ProductRepository
import com.example.groceryapp.data.repository.CartRepository
import com.example.groceryapp.domain.model.Category
import com.example.groceryapp.domain.model.Product
import com.example.groceryapp.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeState {
    object Loading : HomeState()
    data class Success(
        val categories: List<Category>,
        val featuredProducts: List<Product>,
        val freshPicks: List<Product>
    ) : HomeState()
    data class Error(val message: String) : HomeState()
}

class HomeViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val categoryRepository: CategoryRepository = CategoryRepository(),
    private val productRepository: ProductRepository = ProductRepository(),
    private val cartRepository: CartRepository = CartRepository()
) : ViewModel() {
    
    val currentUser: Flow<User?> = authRepository.currentUser
    
    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _state.value = HomeState.Loading
            try {
                var categories = emptyList<Category>()
                var products = emptyList<Product>()

                // Load categories
                categoryRepository.getCategories().collect { result ->
                    result.onSuccess { categories = it }
                }

                // Load all products (we'll slice them for featured/fresh picks)
                productRepository.getProducts().collect { result ->
                    result.onSuccess { products = it }
                }

                _state.value = HomeState.Success(
                    categories = categories,
                    featuredProducts = products.take(6),
                    freshPicks = products.reversed().take(6)
                )
            } catch (e: Exception) {
                _state.value = HomeState.Error(e.message ?: "Failed to load home data")
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            cartRepository.addItem(product.id, 1, product.price)
        }
    }
}
