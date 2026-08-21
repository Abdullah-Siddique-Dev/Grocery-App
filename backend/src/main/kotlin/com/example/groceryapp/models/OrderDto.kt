package com.example.groceryapp.models

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    val deliveryAddress: String
)
