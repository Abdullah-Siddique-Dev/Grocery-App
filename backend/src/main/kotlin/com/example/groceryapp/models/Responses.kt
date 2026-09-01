package com.example.groceryapp.models

import kotlinx.serialization.Serializable

@Serializable
data class CartResponse(
    val id: String?,
    val userId: String,
    val items: List<CartItemResponse>,
    val total: Double,
    val updatedAt: String
)

@Serializable
data class CartItemResponse(
    val productId: String,
    val productName: String,
    val productImageUrl: String,
    val quantity: Int,
    val price: Double,
    val subtotal: Double,
    val stockQuantity: Int
)
