package com.example.groceryapp.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceryapp.data.repository.CartRepository
import com.example.groceryapp.data.repository.FavoriteRepository
import com.example.groceryapp.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class FavoritesState {
    object Loading : FavoritesState()
    data class Success(val products: List<Product>) : FavoritesState()
    object Empty : FavoritesState()
    data class Error(val message: String) : FavoritesState()
}

class FavoritesViewModel(
    private val favoriteRepository: FavoriteRepository = FavoriteRepository(),
    private val cartRepository: CartRepository = CartRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<FavoritesState>(FavoritesState.Loading)
    val state: StateFlow<FavoritesState> = _state.asStateFlow()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _state.value = FavoritesState.Loading
            favoriteRepository.getFavorites().collect { result ->
                result.onSuccess { products ->
                    _state.value = if (products.isEmpty()) FavoritesState.Empty else FavoritesState.Success(products)
                }.onFailure {
                    _state.value = FavoritesState.Error(it.message ?: "Failed to load favorites")
                }
            }
        }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            // In the favorites screen, we assume we are removing it if we toggle
            favoriteRepository.removeFavorite(product.id).onSuccess {
                loadFavorites()
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            cartRepository.addItem(product.id, 1, product.price).onSuccess {
                // Could show a snackbar or similar
            }
        }
    }
}
