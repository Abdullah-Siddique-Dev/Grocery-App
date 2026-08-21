package com.example.groceryapp.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class User(
    val id: String? = null,
    val name: String,
    val email: String,
    val passwordHash: String,
    val phoneNumber: String,
    val address: String,
    val createdAt: String
)

@Serializable
data class Category(
    val id: String? = null,
    val name: String,
    val icon: String,
    val imageUrl: String,
    val displayOrder: Int
)

@Serializable
data class Product(
    val id: String? = null,
    val name: String,
    val description: String,
    val categoryId: String,
    val price: Double,
    val unit: String,
    val imageUrl: String,
    val stockQuantity: Int,
    val isAvailable: Boolean,
    val createdAt: String
)

@Serializable
data class Cart(
    val id: String? = null,
    val userId: String,
    val items: List<CartItem>,
    val updatedAt: String
)

@Serializable
data class CartItem(
    val productId: String,
    val quantity: Int,
    val priceAtAdd: Double
)

@Serializable
data class Order(
    @SerialName("_id")
    val id: String? = null,
    val userId: String,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val deliveryAddress: String,
    val status: String,
    val placedAt: String
)

@Serializable
data class OrderItem(
    val productId: String,
    val quantity: Int,
    val price: Double
)
