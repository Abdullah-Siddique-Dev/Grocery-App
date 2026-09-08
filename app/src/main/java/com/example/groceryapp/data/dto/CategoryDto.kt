package com.example.groceryapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    @SerialName("_id")
    val id: String? = null,
    val name: String,
    val icon: String,
    val imageUrl: String,
    val displayOrder: Int
)
