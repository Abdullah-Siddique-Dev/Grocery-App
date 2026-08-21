package com.example.groceryapp.models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val address: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val user: UserDto,
    val token: String
)

@Serializable
data class UserDto(
    val id: String?,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val createdAt: String
)

@Serializable
data class UserUpdateRequest(
    val name: String,
    val phoneNumber: String,
    val address: String
)

fun User.toDto() = UserDto(
    id = id,
    name = name,
    email = email,
    phoneNumber = phoneNumber,
    address = address,
    createdAt = createdAt
)
