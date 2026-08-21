package com.example.groceryapp.models

import kotlinx.serialization.Serializable

@Serializable
data class CartItemRequest(
    val productId: String,
    val quantity: Int
)

@Serializable
data class UpdateQuantityRequest(
    val quantity: Int
)
