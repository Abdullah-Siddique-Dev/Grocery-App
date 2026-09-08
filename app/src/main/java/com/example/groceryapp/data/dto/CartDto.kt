package com.example.groceryapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartDto(
    @SerialName("id")
    val id: String? = null,
    val userId: String,
    val items: List<CartItemDto>,
    val total: Double,
    val updatedAt: String
)

@Serializable
data class CartItemDto(
    val productId: String,
    val productName: String,
    val productImageUrl: String,
    val quantity: Int,
    val price: Double,
    val subtotal: Double,
    val stockQuantity: Int
)

@Serializable
data class CartItemRequestDto(
    val productId: String,
    val quantity: Int
)

@Serializable
data class UpdateQuantityRequestDto(
    val quantity: Int
)
