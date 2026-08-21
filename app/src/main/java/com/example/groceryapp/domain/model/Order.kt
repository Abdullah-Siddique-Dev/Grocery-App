package com.example.groceryapp.domain.model

data class Order(
    val id: String,
    val userId: String,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val deliveryAddress: String,
    val status: String,
    val placedAt: String
)

data class OrderItem(
    val productId: String,
    val quantity: Int,
    val price: Double
)
