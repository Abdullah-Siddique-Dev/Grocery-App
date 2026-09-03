package com.example.groceryapp.services

import com.example.groceryapp.models.Address
import com.example.groceryapp.models.User
import com.example.groceryapp.models.UserUpdateRequest
import com.example.groceryapp.models.toDto
import com.example.groceryapp.repositories.UserRepository

class UserService(private val repository: UserRepository = UserRepository()) {

    suspend fun getUserProfile(userId: String): Result<User> {
        val user = repository.findById(userId) 
            ?: return Result.failure(Exception("User not found"))
        return Result.success(user)
    }

    suspend fun updateProfile(userId: String, request: UserUpdateRequest): Result<User> {
        val address = Address(
            fullName = request.name,
            phoneNumber = request.phoneNumber,
            addressLine = request.address,
            city = "",
            postalCode = ""
        )
        val updated = repository.update(userId, request.name, request.phoneNumber, address)
            ?: return Result.failure(Exception("Failed to update profile"))
        return Result.success(updated)
    }

    suspend fun updateAddress(userId: String, address: Address): Result<User> {
        val updated = repository.updateAddress(userId, address)
            ?: return Result.failure(Exception("Failed to update address"))
        return Result.success(updated)
    }

    suspend fun updateFcmToken(userId: String, token: String?): Result<Unit> {
        val success = repository.updateFcmToken(userId, token)
        return if (success) Result.success(Unit)
        else Result.failure(Exception("Failed to update FCM token"))
    }

    suspend fun getFcmToken(userId: String): String? {
        val user = repository.findById(userId)
        return user?.fcmToken
    }

    suspend fun getAllUsers(): List<User> {
        return repository.findAll()
    }
}
