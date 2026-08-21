package com.example.groceryapp.services

import com.example.groceryapp.models.UserDto
import com.example.groceryapp.models.UserUpdateRequest
import com.example.groceryapp.models.toDto
import com.example.groceryapp.repositories.UserRepository

class UserService(private val userRepository: UserRepository = UserRepository()) {

    suspend fun getUserProfile(userId: String): Result<UserDto> {
        val user = userRepository.findById(userId) ?: return Result.failure(Exception("User not found"))
        return Result.success(user.toDto())
    }

    suspend fun updateProfile(userId: String, request: UserUpdateRequest): Result<UserDto> {
        if (request.name.isBlank() || request.phoneNumber.isBlank() || request.address.isBlank()) {
            return Result.failure(Exception("Name, phone number, and address are required"))
        }

        val updatedUser = userRepository.update(
            id = userId,
            name = request.name,
            phoneNumber = request.phoneNumber,
            address = request.address
        ) ?: return Result.failure(Exception("Failed to update profile"))

        return Result.success(updatedUser.toDto())
    }
}
