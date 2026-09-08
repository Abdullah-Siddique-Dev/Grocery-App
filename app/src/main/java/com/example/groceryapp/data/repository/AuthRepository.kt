package com.example.groceryapp.data.repository

import com.example.groceryapp.data.dto.LoginRequestDto
import com.example.groceryapp.data.dto.RegisterRequestDto
import com.example.groceryapp.data.dto.AuthResponseDto
import com.example.groceryapp.data.dto.toDomain
import com.example.groceryapp.data.network.ApiClient
import com.example.groceryapp.data.network.InMemoryTokenProvider
import com.example.groceryapp.data.network.TokenProvider
import com.example.groceryapp.domain.model.User
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

import com.example.groceryapp.data.dto.ErrorResponseDto
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

class AuthRepository(
    private val apiClient: ApiClient = ApiClient(InMemoryTokenProvider.getInstance()),
    private val tokenProvider: TokenProvider = InMemoryTokenProvider.getInstance()
) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: Flow<User?> = _currentUser.asStateFlow()

    private suspend fun parseErrorMessage(response: HttpResponse, defaultMsg: String): String {
        return try {
            val errorObj = response.body<ErrorResponseDto>()
            errorObj.message
        } catch (e: Exception) {
            try {
                val rawText = response.bodyAsText()
                if (rawText.isNotBlank()) rawText else defaultMsg
            } catch (e2: Exception) {
                defaultMsg
            }
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = apiClient.client.post("/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequestDto(email, password))
            }
            
            if (response.status.value in 200..299) {
                val authResponse = response.body<AuthResponseDto>()
                tokenProvider.saveToken(authResponse.token)
                val user = authResponse.user.toDomain()
                _currentUser.value = user
                Result.success(user)
            } else {
                val errorMsg = parseErrorMessage(response, "Login failed: ${response.status}")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        phoneNumber: String,
        address: String
    ): Result<User> {
        return try {
            val response = apiClient.client.post("/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequestDto(name, email, password, phoneNumber, address))
            }
            
            if (response.status.value in 200..299) {
                val authResponse = response.body<AuthResponseDto>()
                tokenProvider.saveToken(authResponse.token)
                val user = authResponse.user.toDomain()
                _currentUser.value = user
                Result.success(user)
            } else {
                val errorMsg = parseErrorMessage(response, "Registration failed: ${response.status}")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        try {
            apiClient.client.post("/auth/logout")
        } catch (e: Exception) {
            // Log error but proceed to clear local state
        } finally {
            tokenProvider.clearToken()
            _currentUser.value = null
        }
    }

    fun isAuthenticated(): Boolean {
        return _currentUser.value != null
    }
}
