package com.example.groceryapp.domain.model

data class Cart(
    val id: String,
    val userId: String,
    val items: List<CartItem>,
    val updatedAt: String
)

data class CartItem(
    val productId: String,
    val quantity: Int,
    val priceAtAdd: Double
)
