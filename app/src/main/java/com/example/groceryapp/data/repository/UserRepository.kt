package com.example.groceryapp.data.repository

import com.example.groceryapp.data.dto.UserDto
import com.example.groceryapp.data.dto.UserUpdateRequestDto
import com.example.groceryapp.data.dto.toDomain
import com.example.groceryapp.data.network.ApiClient
import com.example.groceryapp.data.network.InMemoryTokenProvider
import com.example.groceryapp.domain.model.User
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepository(
    private val apiClient: ApiClient = ApiClient(InMemoryTokenProvider.getInstance())
) {
    fun getUserProfile(userId: String): Flow<Result<User>> = flow {
        try {
            val response = apiClient.client.get("/user/profile")
            if (response.status.value in 200..299) {
                val dto = response.body<UserDto>()
                emit(Result.success(dto.toDomain()))
            } else {
                emit(Result.failure(Exception("Failed to fetch profile: ${response.status}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun updateProfile(user: User): Result<User> {
        return try {
            val response = apiClient.client.put("/user/profile") {
                contentType(ContentType.Application.Json)
                setBody(UserUpdateRequestDto(
                    name = user.name,
                    phoneNumber = user.phoneNumber,
                    address = user.address
                ))
            }
            if (response.status.value in 200..299) {
                Result.success(response.body<UserDto>().toDomain())
            } else {
                Result.failure(Exception("Failed to update profile: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
