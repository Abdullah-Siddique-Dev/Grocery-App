package com.example.groceryapp.domain.model

data class Category(
    val id: String,
    val name: String,
    val icon: String,
    val imageUrl: String,
    val displayOrder: Int
)
