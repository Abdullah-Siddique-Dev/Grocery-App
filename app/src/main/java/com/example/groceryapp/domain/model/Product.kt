package com.example.groceryapp.domain.model

data class Product(
    val id: String,
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
