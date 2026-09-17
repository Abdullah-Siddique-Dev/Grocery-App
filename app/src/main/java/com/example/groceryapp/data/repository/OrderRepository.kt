package com.example.groceryapp.data.repository

import com.example.groceryapp.data.cache.CacheManager
import com.example.groceryapp.data.dto.OrderDto
import com.example.groceryapp.data.dto.OrderRequestDto
import com.example.groceryapp.data.dto.toDomain
import com.example.groceryapp.data.dto.toDto
import com.example.groceryapp.data.network.ApiClient
import com.example.groceryapp.data.network.InMemoryTokenProvider
import com.example.groceryapp.domain.model.Order
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration.Companion.minutes

/**
 * Order repository with caching for order history.
 * 
 * Caching strategy:
 * - Order history: 3 minutes TTL (balances freshness and performance)
 * - Order details: 5 minutes TTL
 * - Cache invalidated after placing/cancelling orders
 * 
 * Expected performance:
 * - Cache hit: <50ms
 * - Cache miss: 500-1000ms
 * - 60%+ cache hit rate
 */
class OrderRepository(
    private val apiClient: ApiClient = ApiClient(InMemoryTokenProvider.getInstance())
) : BaseRepository() {
    
    private val orderListCache = CacheManager<List<Order>>()
    private val orderDetailCache = CacheManager<Order>()
    
    /**
     * Place a new order.
     * Invalidates order caches on success.
     */
    suspend fun placeOrder(
        deliveryAddress: com.example.groceryapp.domain.model.Address,
        paymentMethod: com.example.groceryapp.domain.model.PaymentMethod
    ): Result<Order> {
        return try {
            val response = apiClient.client.post("/orders") {
                contentType(ContentType.Application.Json)
                setBody(OrderRequestDto(deliveryAddress.toDto(), paymentMethod))
            }
            if (response.status.value in 200..299) {
                val order = response.body<OrderDto>().toDomain()
                // Invalidate caches so next fetch gets updated list
                orderListCache.clear()
                Result.success(order)
            } else {
                Result.failure(Exception("Failed to place order: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get user's order history.
     * Results are cached for 3 minutes.
     */
    suspend fun getOrderHistory(): Flow<Result<List<Order>>> {
        return fetchWithCache(
            cacheKey = "order_history",
            cache = orderListCache,
            ttl = 3.minutes,
            transform = { dtos: List<OrderDto> -> dtos.map { it.toDomain() } }
        ) {
            apiClient.client.get("/orders")
        }
    }
    
    /**
     * Get order details by ID.
     * Results are cached for 5 minutes.
     */
    suspend fun getOrderDetails(orderId: String): Flow<Result<Order>> {
        return fetchWithCache(
            cacheKey = "order_$orderId",
            cache = orderDetailCache,
            ttl = 5.minutes,
            transform = { dto: OrderDto -> dto.toDomain() }
        ) {
            apiClient.client.get("/orders/$orderId")
        }
    }
    
    /**
     * Cancel an order.
     * Invalidates order caches on success.
     */
    suspend fun cancelOrder(orderId: String): Result<Unit> {
        return try {
            val response = apiClient.client.patch("/orders/$orderId/cancel")
            if (response.status.value in 200..299) {
                // Invalidate caches so next fetch reflects cancellation
                orderListCache.clear()
                orderDetailCache.invalidate("order_$orderId")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to cancel order: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
