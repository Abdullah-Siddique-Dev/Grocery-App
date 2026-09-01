package com.example.groceryapp.services

import com.example.groceryapp.models.*
import com.example.groceryapp.repositories.UserRepository

class UserService(private val userRepository: UserRepository = UserRepository()) {

    suspend fun getUserProfile(userId: String): Result<UserDto> {
        val user = userRepository.findById(userId) ?: return Result.failure(Exception("User not found"))
        return Result.success(user.toDto())
    }

    suspend fun updateProfile(userId: String, request: UserUpdateRequest): Result<UserDto> {
        if (request.name.isBlank() || request.phoneNumber.isBlank()) {
            return Result.failure(Exception("Name and phone number are required"))
        }

        val updatedUser = userRepository.update(
            id = userId,
            name = request.name,
            phoneNumber = request.phoneNumber,
            address = null 
        ) ?: return Result.failure(Exception("Failed to update profile"))

        return Result.success(updatedUser.toDto())
    }

    suspend fun updateAddress(userId: String, address: Address): Result<UserDto> {
        if (address.addressLine.isBlank() || address.city.isBlank()) {
            return Result.failure(Exception("Address and City are required"))
        }

        val updatedUser = userRepository.updateAddress(userId, address) 
            ?: return Result.failure(Exception("Failed to update address"))
            
        return Result.success(updatedUser.toDto())
    }

    suspend fun updateFcmToken(userId: String, token: String?): Result<Unit> {
        val success = userRepository.updateFcmToken(userId, token)
        return if (success) Result.success(Unit) else Result.failure(Exception("Failed to update token"))
    }

    suspend fun getFcmToken(userId: String): String? {
        return userRepository.findById(userId)?.fcmToken
    }

    suspend fun getAllUsers(): List<UserDto> {
        return userRepository.findAll().map { it.toDto() }
    }
}
