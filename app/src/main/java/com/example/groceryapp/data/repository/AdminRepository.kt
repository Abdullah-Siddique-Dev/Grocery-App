package com.example.groceryapp.data.repository

import com.example.groceryapp.data.dto.*
import com.example.groceryapp.data.network.ApiClient
import com.example.groceryapp.data.network.InMemoryTokenProvider
import com.example.groceryapp.domain.model.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AdminRepository(
    private val apiClient: ApiClient = ApiClient(InMemoryTokenProvider.getInstance())
) {
    // Orders
    fun getAllOrders(): Flow<Result<List<Order>>> = flow {
        try {
            val response = apiClient.client.get("/admin/orders")
            if (response.status.value in 200..299) {
                val dtos = response.body<List<OrderDto>>()
                emit(Result.success(dtos.map { it.toDomain() }))
            } else {
                emit(Result.failure(Exception("Failed to fetch orders: ${response.status}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit> {
        return try {
            val response = apiClient.client.patch("/admin/orders/$orderId/status") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("status" to status.name))
            }
            if (response.status.value in 200..299) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to update status: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Products
    fun getAllProducts(): Flow<Result<List<Product>>> = flow {
        try {
            val response = apiClient.client.get("/admin/products")
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

    suspend fun createProduct(product: Product): Result<Product> {
        return try {
            val response = apiClient.client.post("/admin/products") {
                contentType(ContentType.Application.Json)
                setBody(product.toDto())
            }
            if (response.status.value in 200..299) {
                Result.success(response.body<ProductDto>().toDomain())
            } else {
                Result.failure(Exception("Failed to create product: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(id: String, product: Product): Result<Product> {
        return try {
            val response = apiClient.client.patch("/admin/products/$id") {
                contentType(ContentType.Application.Json)
                setBody(product.toDto())
            }
            if (response.status.value in 200..299) {
                Result.success(response.body<ProductDto>().toDomain())
            } else {
                Result.failure(Exception("Failed to update product: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(id: String): Result<Unit> {
        return try {
            val response = apiClient.client.delete("/admin/products/$id")
            if (response.status.value in 200..299) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete product: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Categories
    fun getAllCategories(): Flow<Result<List<Category>>> = flow {
        try {
            val response = apiClient.client.get("/admin/categories")
            if (response.status.value in 200..299) {
                val dtos = response.body<List<CategoryDto>>()
                emit(Result.success(dtos.map { it.toDomain() }))
            } else {
                emit(Result.failure(Exception("Failed to fetch categories: ${response.status}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun createCategory(category: Category): Result<Category> {
        return try {
            val response = apiClient.client.post("/admin/categories") {
                contentType(ContentType.Application.Json)
                setBody(category.toDto())
            }
            if (response.status.value in 200..299) {
                Result.success(response.body<CategoryDto>().toDomain())
            } else {
                Result.failure(Exception("Failed to create category: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCategory(id: String, category: Category): Result<Category> {
        return try {
            val response = apiClient.client.patch("/admin/categories/$id") {
                contentType(ContentType.Application.Json)
                setBody(category.toDto())
            }
            if (response.status.value in 200..299) {
                Result.success(response.body<CategoryDto>().toDomain())
            } else {
                Result.failure(Exception("Failed to update category: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(id: String): Result<Unit> {
        return try {
            val response = apiClient.client.delete("/admin/categories/$id")
            if (response.status.value in 200..299) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete category: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Users
    fun getAllUsers(): Flow<Result<List<User>>> = flow {
        try {
            val response = apiClient.client.get("/admin/users")
            if (response.status.value in 200..299) {
                val dtos = response.body<List<UserDto>>()
                emit(Result.success(dtos.map { it.toDomain() }))
            } else {
                emit(Result.failure(Exception("Failed to fetch users: ${response.status}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
