package com.example.groceryapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    @SerialName("_id")
    val id: String?,
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
