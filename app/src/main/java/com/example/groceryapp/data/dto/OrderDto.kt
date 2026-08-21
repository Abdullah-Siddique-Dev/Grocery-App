package com.example.groceryapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    @SerialName("_id")
    val id: String?,
    val userId: String,
    val items: List<OrderItemDto>,
    val totalAmount: Double,
    val deliveryAddress: String,
    val status: String,
    val placedAt: String
)

@Serializable
data class OrderItemDto(
    val productId: String,
    val quantity: Int,
    val price: Double
)

@Serializable
data class OrderRequestDto(
    val deliveryAddress: String
)
