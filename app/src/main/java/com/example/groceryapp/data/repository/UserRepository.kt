package com.example.groceryapp.data.repository

import com.example.groceryapp.data.cache.CacheManager
import com.example.groceryapp.data.dto.UserDto
import com.example.groceryapp.data.dto.UserUpdateRequestDto
import com.example.groceryapp.data.dto.toDomain
import com.example.groceryapp.data.dto.toDto
import com.example.groceryapp.data.network.ApiClient
import com.example.groceryapp.data.network.InMemoryTokenProvider
import com.example.groceryapp.domain.model.Address
import com.example.groceryapp.domain.model.User
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration.Companion.minutes

/**
 * User repository with caching for profile data.
 * 
 * Caching strategy:
 * - User profile: 5 minutes TTL
 * - Cache updated after mutations (update profile/address)
 * - Session-level caching
 * 
 * Expected performance:
 * - Cache hit: <50ms
 * - Cache miss: 300-500ms
 * - 80%+ cache hit rate (profile viewed frequently)
 */
class UserRepository(
    private val apiClient: ApiClient = ApiClient(InMemoryTokenProvider.getInstance())
) : BaseRepository() {
    
    private val userCache = CacheManager<User>()
    
    /**
     * Get user profile.
     * Results are cached for 5 minutes.
     */
    suspend fun getUserProfile(): Flow<Result<User>> {
        return fetchWithCache(
            cacheKey = "user_profile",
            cache = userCache,
            ttl = 5.minutes,
            transform = { dto: UserDto -> dto.toDomain() }
        ) {
            apiClient.client.get("/user/profile")
        }
    }
    
    /**
     * Update user profile.
     * Updates cache on success.
     */
    suspend fun updateProfile(name: String, phoneNumber: String): Result<User> {
        return try {
            val response = apiClient.client.put("/user/profile") {
                contentType(ContentType.Application.Json)
                setBody(UserUpdateRequestDto(
                    name = name,
                    phoneNumber = phoneNumber
                ))
            }
            if (response.status.value in 200..299) {
                val user = response.body<UserDto>().toDomain()
                // Update cache with new profile data
                userCache.put("user_profile", user, ttl = 5.minutes)
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to update profile: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update user address.
     * Updates cache on success.
     */
    suspend fun updateAddress(address: Address): Result<User> {
        return try {
            val response = apiClient.client.put("/user/profile/address") {
                contentType(ContentType.Application.Json)
                setBody(address.toDto())
            }
            if (response.status.value in 200..299) {
                val user = response.body<UserDto>().toDomain()
                // Update cache with new profile data
                userCache.put("user_profile", user, ttl = 5.minutes)
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to update address: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update FCM token for push notifications.
     * Does not cache (not user-visible data).
     */
    suspend fun updateFcmToken(token: String?): Result<Unit> {
        return try {
            val response = apiClient.client.post("/user/profile/fcm-token") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("token" to token))
            }
            if (response.status.value in 200..299) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to update FCM token: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
