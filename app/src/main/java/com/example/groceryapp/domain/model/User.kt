package com.example.groceryapp.domain.model

enum class UserRole {
    CUSTOMER,
    ADMIN
}

data class Address(
    val fullName: String,
    val phoneNumber: String,
    val addressLine: String,
    val city: String,
    val postalCode: String
)

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val address: Address?,
    val role: UserRole,
    val createdAt: String
)
