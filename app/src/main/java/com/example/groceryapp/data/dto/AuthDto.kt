package com.example.groceryapp.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val address: String
)

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponseDto(
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
data class UserUpdateRequestDto(
    val name: String,
    val phoneNumber: String,
    val address: String
)
