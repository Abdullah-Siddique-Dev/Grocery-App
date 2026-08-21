package com.example.groceryapp.data.repository

import com.example.groceryapp.data.dto.*
import com.example.groceryapp.data.network.ApiClient
import com.example.groceryapp.data.network.InMemoryTokenProvider
import com.example.groceryapp.domain.model.Cart
import com.example.groceryapp.domain.model.CartItem
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CartRepository(
    private val apiClient: ApiClient = ApiClient(InMemoryTokenProvider.getInstance())
) {

    fun getCart(): Flow<Result<Cart>> = flow {
        try {
            val response = apiClient.client.get("/cart")
            if (response.status.value in 200..299) {
                val dto = response.body<CartDto>()
                emit(Result.success(dto.toDomain()))
            } else {
                emit(Result.failure(Exception("Failed to fetch cart: ${response.status}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun addItem(productId: String, quantity: Int, priceAtAdd: Double): Result<Cart> {
        return try {
            val response = apiClient.client.post("/cart/items") {
                contentType(ContentType.Application.Json)
                setBody(CartItemRequestDto(productId, quantity))
            }
            if (response.status.value in 200..299) {
                Result.success(response.body<CartDto>().toDomain())
            } else {
                Result.failure(Exception("Failed to add item: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateQuantity(productId: String, quantity: Int): Result<Cart> {
        return try {
            val response = apiClient.client.put("/cart/items/$productId") {
                contentType(ContentType.Application.Json)
                setBody(UpdateQuantityRequestDto(quantity))
            }
            if (response.status.value in 200..299) {
                Result.success(response.body<CartDto>().toDomain())
            } else {
                Result.failure(Exception("Failed to update quantity: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeItem(productId: String): Result<Cart> {
        return try {
            val response = apiClient.client.delete("/cart/items/$productId")
            if (response.status.value in 200..299) {
                Result.success(response.body<CartDto>().toDomain())
            } else {
                Result.failure(Exception("Failed to remove item: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearCart(): Result<Unit> {
        return try {
            val response = apiClient.client.delete("/cart")
            if (response.status.value in 200..299) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to clear cart: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
