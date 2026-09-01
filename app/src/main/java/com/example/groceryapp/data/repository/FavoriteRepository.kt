package com.example.groceryapp.data.repository

import com.example.groceryapp.data.dto.ProductDto
import com.example.groceryapp.data.dto.toDomain
import com.example.groceryapp.data.network.ApiClient
import com.example.groceryapp.data.network.InMemoryTokenProvider
import com.example.groceryapp.domain.model.Product
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteCheckResponse(val isFavorited: Boolean)

class FavoriteRepository(
    private val apiClient: ApiClient = ApiClient(InMemoryTokenProvider.getInstance())
) {

    fun getFavorites(): Flow<Result<List<Product>>> = flow {
        try {
            val response = apiClient.client.get("/favorites")
            if (response.status.value in 200..299) {
                val dtos = response.body<List<ProductDto>>()
                emit(Result.success(dtos.map { it.toDomain() }))
            } else {
                emit(Result.failure(Exception("Failed to fetch favorites: ${response.status}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun addFavorite(productId: String): Result<Unit> {
        return try {
            val response = apiClient.client.post("/favorites/$productId")
            if (response.status.value in 200..299) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to add favorite: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFavorite(productId: String): Result<Unit> {
        return try {
            val response = apiClient.client.delete("/favorites/$productId")
            if (response.status.value in 200..299) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to remove favorite: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isFavorited(productId: String): Result<Boolean> {
        return try {
            val response = apiClient.client.get("/favorites/check/$productId")
            if (response.status.value in 200..299) {
                Result.success(response.body<FavoriteCheckResponse>().isFavorited)
            } else {
                Result.failure(Exception("Failed to check favorite: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
