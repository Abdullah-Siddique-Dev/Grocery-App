package com.example.groceryapp.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val createdAt: String
)
