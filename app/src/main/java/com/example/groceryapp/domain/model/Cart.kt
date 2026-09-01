package com.example.groceryapp.domain.model

data class Cart(
    val id: String,
    val userId: String,
    val items: List<CartItem>,
    val total: Double,
    val updatedAt: String
)

data class CartItem(
    val productId: String,
    val productName: String,
    val productImageUrl: String,
    val quantity: Int,
    val price: Double,
    val subtotal: Double,
    val stockQuantity: Int
)
