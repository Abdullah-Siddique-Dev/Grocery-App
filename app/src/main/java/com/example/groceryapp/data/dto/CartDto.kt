package com.example.groceryapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartDto(
    @SerialName("_id")
    val id: String?,
    val userId: String,
    val items: List<CartItemDto>,
    val updatedAt: String
)

@Serializable
data class CartItemDto(
    val productId: String,
    val quantity: Int,
    val priceAtAdd: Double
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
