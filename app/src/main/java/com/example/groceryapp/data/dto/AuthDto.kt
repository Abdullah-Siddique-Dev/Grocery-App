package com.example.groceryapp.data.dto

import com.example.groceryapp.domain.model.UserRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val address: String? = null // Registration might still use a string or we structured it later
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
data class AddressDto(
    val fullName: String,
    val phoneNumber: String,
    val addressLine: String,
    val city: String,
    val postalCode: String
)

@Serializable
data class UserDto(
    @SerialName("_id")
    val id: String?,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val address: AddressDto? = null,
    val role: UserRole,
    val createdAt: String
)

@Serializable
data class UserUpdateRequestDto(
    val name: String,
    val phoneNumber: String
)
