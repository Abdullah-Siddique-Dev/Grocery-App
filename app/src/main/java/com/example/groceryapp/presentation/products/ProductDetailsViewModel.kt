package com.example.groceryapp.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceryapp.data.repository.CartRepository
import com.example.groceryapp.data.repository.FavoriteRepository
import com.example.groceryapp.data.repository.ProductRepository
import com.example.groceryapp.data.repository.ReviewRepository
import com.example.groceryapp.domain.model.Product
import com.example.groceryapp.domain.model.ProductReviews
import com.example.groceryapp.domain.model.Review
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProductDetailsState {
    object Loading : ProductDetailsState()
    data class Success(val product: Product) : ProductDetailsState()
    data class Error(val message: String) : ProductDetailsState()
}

sealed class ReviewsState {
    object Loading : ReviewsState()
    data class Success(val reviews: ProductReviews) : ReviewsState()
    data class Error(val message: String) : ReviewsState()
}

class ProductDetailsViewModel(
    private val repository: ProductRepository = ProductRepository(),
    private val cartRepository: CartRepository = CartRepository(),
    private val favoriteRepository: FavoriteRepository = FavoriteRepository(),
    private val reviewRepository: ReviewRepository = ReviewRepository()
) : ViewModel() {
    private val _state = MutableStateFlow<ProductDetailsState>(ProductDetailsState.Loading)
    val state: StateFlow<ProductDetailsState> = _state.asStateFlow()

    private val _reviewsState = MutableStateFlow<ReviewsState>(ReviewsState.Loading)
    val reviewsState: StateFlow<ReviewsState> = _reviewsState.asStateFlow()

    private val _isAddedToCart = MutableStateFlow(false)
    val isAddedToCart: StateFlow<Boolean> = _isAddedToCart.asStateFlow()

    private val _isFavorited = MutableStateFlow(false)
    val isFavorited: StateFlow<Boolean> = _isFavorited.asStateFlow()

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _state.value = ProductDetailsState.Loading
            
            // Check favorite status
            favoriteRepository.isFavorited(productId).onSuccess {
                _isFavorited.value = it
            }

            // Load reviews
            loadReviews(productId)

            repository.getProductById(productId).collect { result ->
                result.onSuccess { product ->
                    _state.value = ProductDetailsState.Success(product)
                }.onFailure {
                    _state.value = ProductDetailsState.Error(it.message ?: "Unknown error")
                }
            }
        }
    }

    private fun loadReviews(productId: String) {
        viewModelScope.launch {
            _reviewsState.value = ReviewsState.Loading
            reviewRepository.getProductReviews(productId).collect { result ->
                result.onSuccess { reviews ->
                    _reviewsState.value = ReviewsState.Success(reviews)
                }.onFailure {
                    _reviewsState.value = ReviewsState.Error(it.message ?: "Failed to load reviews")
                }
            }
        }
    }

    fun submitReview(productId: String, rating: Int, comment: String) {
        viewModelScope.launch {
            reviewRepository.addReview(productId, rating, comment).onSuccess {
                loadReviews(productId)
            }.onFailure {
                // Handle failure
            }
        }
    }

    fun deleteReview(productId: String, reviewId: String) {
        viewModelScope.launch {
            reviewRepository.deleteReview(productId, reviewId).onSuccess {
                loadReviews(productId)
            }
        }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            if (_isFavorited.value) {
                favoriteRepository.removeFavorite(product.id).onSuccess {
                    _isFavorited.value = false
                }
            } else {
                favoriteRepository.addFavorite(product.id).onSuccess {
                    _isFavorited.value = true
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
