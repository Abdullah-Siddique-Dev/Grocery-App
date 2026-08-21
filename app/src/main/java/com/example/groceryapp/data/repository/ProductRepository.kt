package com.example.groceryapp.data.repository

import com.example.groceryapp.data.dto.ProductDto
import com.example.groceryapp.data.dto.toDomain
import com.example.groceryapp.data.network.ApiClient
import com.example.groceryapp.data.network.InMemoryTokenProvider
import com.example.groceryapp.domain.model.Product
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductRepository(
    private val apiClient: ApiClient = ApiClient(InMemoryTokenProvider.getInstance())
) {
    fun getProducts(categoryId: String? = null, query: String? = null): Flow<Result<List<Product>>> = flow {
        try {
            val response = apiClient.client.get("/products") {
                if (categoryId != null) parameter("categoryId", categoryId)
                if (query != null) parameter("q", query)
            }
            if (response.status.value in 200..299) {
                val dtos = response.body<List<ProductDto>>()
                emit(Result.success(dtos.map { it.toDomain() }))
            } else {
                emit(Result.failure(Exception("Failed to fetch products: ${response.status}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getProductById(productId: String): Flow<Result<Product>> = flow {
        try {
            val response = apiClient.client.get("/products/$productId")
            if (response.status.value in 200..299) {
                val dto = response.body<ProductDto>()
                emit(Result.success(dto.toDomain()))
            } else {
                emit(Result.failure(Exception("Failed to fetch product: ${response.status}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
