package com.example.groceryapp.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groceryapp.data.repository.AdminRepository
import com.example.groceryapp.data.repository.CategoryRepository
import com.example.groceryapp.data.repository.ProductRepository
import com.example.groceryapp.domain.model.Category
import com.example.groceryapp.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant

sealed class AdminProductEditState {
    object Idle : AdminProductEditState()
    object Loading : AdminProductEditState()
    object Success : AdminProductEditState()
    data class Error(val message: String) : AdminProductEditState()
}

class AdminProductEditViewModel(
    private val adminRepository: AdminRepository = AdminRepository(),
    private val productRepository: ProductRepository = ProductRepository(),
    private val categoryRepository: CategoryRepository = CategoryRepository()
) : ViewModel() {
    private val _state = MutableStateFlow<AdminProductEditState>(AdminProductEditState.Idle)
    val state: StateFlow<AdminProductEditState> = _state.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getCategories().collect { result ->
                result.onSuccess { _categories.value = it }
            }
        }
    }

    fun loadProduct(id: String) {
        if (id == "new") {
            _product.value = null
            return
        }
        viewModelScope.launch {
            _state.value = AdminProductEditState.Loading
            productRepository.getProductById(id).collect { result ->
                result.onSuccess { 
                    _product.value = it
                    _state.value = AdminProductEditState.Idle
                }.onFailure {
                    _state.value = AdminProductEditState.Error("Product not found")
                }
            }
        }
    }

    fun saveProduct(
        id: String?,
        name: String,
        description: String,
        categoryId: String,
        price: Double,
        unit: String,
        imageUrl: String,
        stockQuantity: Int,
        isAvailable: Boolean
    ) {
        viewModelScope.launch {
            _state.value = AdminProductEditState.Loading
            val productToSave = Product(
                id = id ?: "",
                name = name,
                description = description,
                categoryId = categoryId,
                price = price,
                unit = unit,
                imageUrl = imageUrl,
                stockQuantity = stockQuantity,
                isAvailable = isAvailable,
                createdAt = _product.value?.createdAt ?: ""
            )

            val result = if (id == null) {
                adminRepository.createProduct(productToSave)
            } else {
                adminRepository.updateProduct(id, productToSave)
            }

            result.onSuccess {
                _state.value = AdminProductEditState.Success
            }.onFailure {
                _state.value = AdminProductEditState.Error(it.message ?: "Failed to save product")
            }
        }
    }
}
