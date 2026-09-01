package com.example.groceryapp.data.repository

import com.example.groceryapp.data.dto.OrderDto
import com.example.groceryapp.data.dto.OrderRequestDto
import com.example.groceryapp.data.dto.toDomain
import com.example.groceryapp.data.dto.toDto
import com.example.groceryapp.data.network.ApiClient
import com.example.groceryapp.data.network.InMemoryTokenProvider
import com.example.groceryapp.domain.model.Order
import com.example.groceryapp.domain.model.OrderItem
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OrderRepository(
    private val apiClient: ApiClient = ApiClient(InMemoryTokenProvider.getInstance())
) {

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
                Result.success(response.body<OrderDto>().toDomain())
            } else {
                Result.failure(Exception("Failed to place order: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getOrderHistory(): Flow<Result<List<Order>>> = flow {
        try {
            val response = apiClient.client.get("/orders")
            if (response.status.value in 200..299) {
                val dtos = response.body<List<OrderDto>>()
                emit(Result.success(dtos.map { it.toDomain() }))
            } else {
                emit(Result.failure(Exception("Failed to fetch order history: ${response.status}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getOrderDetails(orderId: String): Flow<Result<Order>> = flow {
        try {
            val response = apiClient.client.get("/orders/$orderId")
            if (response.status.value in 200..299) {
                emit(Result.success(response.body<OrderDto>().toDomain()))
            } else {
                emit(Result.failure(Exception("Failed to fetch order details: ${response.status}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun cancelOrder(orderId: String): Result<Unit> {
        return try {
            val response = apiClient.client.patch("/orders/$orderId/cancel")
            if (response.status.value in 200..299) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to cancel order: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
