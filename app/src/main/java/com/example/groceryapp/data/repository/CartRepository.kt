package com.example.groceryapp.data.repository

import com.example.groceryapp.data.cache.CacheManager
import com.example.groceryapp.data.dto.*
import com.example.groceryapp.data.network.ApiClient
import com.example.groceryapp.data.network.InMemoryTokenProvider
import com.example.groceryapp.domain.model.Cart
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration.Companion.minutes

/**
 * Cart repository with caching for user's cart.
 * 
 * Caching strategy:
 * - Cart is cached for 2 minutes (short TTL due to frequent updates)
 * - Cache invalidated after mutations (add/update/remove)
 * - Real-time consistency prioritized
 * 
 * Expected performance:
 * - Cache hit: <50ms
 * - Cache miss: 300-500ms
 * - Mutations: Bypass cache, refresh on success
 */
class CartRepository(
    private val apiClient: ApiClient = ApiClient(InMemoryTokenProvider.getInstance())
) : BaseRepository() {
    
    private val cartCache = CacheManager<Cart>()
    
    /**
     * Get user's cart.
     * Results are cached for 2 minutes.
     */
    suspend fun getCart(): Flow<Result<Cart>> {
        return fetchWithCache(
            cacheKey = "user_cart",
            cache = cartCache,
            ttl = 2.minutes,
            transform = { dto: CartDto -> dto.toDomain() }
        ) {
            apiClient.client.get("/cart")
        }
    }
    
    /**
     * Add item to cart.
     * Invalidates cache on success.
     */
    suspend fun addItem(productId: String, quantity: Int, priceAtAdd: Double): Result<Cart> {
        return try {
            val response = apiClient.client.post("/cart/items") {
                contentType(ContentType.Application.Json)
                setBody(CartItemRequestDto(productId, quantity))
            }
            if (response.status.value in 200..299) {
                val cart = response.body<CartDto>().toDomain()
                // Update cache with new cart
                cartCache.put("user_cart", cart, ttl = 2.minutes)
                Result.success(cart)
            } else {
                Result.failure(Exception("Failed to add item: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update item quantity in cart.
     * Invalidates cache on success.
     */
    suspend fun updateQuantity(productId: String, quantity: Int): Result<Cart> {
        return try {
            val response = apiClient.client.put("/cart/items/$productId") {
                contentType(ContentType.Application.Json)
                setBody(UpdateQuantityRequestDto(quantity))
            }
            if (response.status.value in 200..299) {
                val cart = response.body<CartDto>().toDomain()
                // Update cache with new cart
                cartCache.put("user_cart", cart, ttl = 2.minutes)
                Result.success(cart)
            } else {
                Result.failure(Exception("Failed to update quantity: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Remove item from cart.
     * Invalidates cache on success.
     */
    suspend fun removeItem(productId: String): Result<Cart> {
        return try {
            val response = apiClient.client.delete("/cart/items/$productId")
            if (response.status.value in 200..299) {
                val cart = response.body<CartDto>().toDomain()
                // Update cache with new cart
                cartCache.put("user_cart", cart, ttl = 2.minutes)
                Result.success(cart)
            } else {
                Result.failure(Exception("Failed to remove item: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Clear entire cart.
     * Invalidates cache on success.
     */
    suspend fun clearCart(): Result<Unit> {
        return try {
            val response = apiClient.client.delete("/cart")
            if (response.status.value in 200..299) {
                // Clear cart cache
                cartCache.invalidate("user_cart")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to clear cart: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
