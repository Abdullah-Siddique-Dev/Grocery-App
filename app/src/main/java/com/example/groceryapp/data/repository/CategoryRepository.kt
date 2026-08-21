package com.example.groceryapp.data.repository

import com.example.groceryapp.data.dto.CategoryDto
import com.example.groceryapp.data.dto.toDomain
import com.example.groceryapp.data.network.ApiClient
import com.example.groceryapp.data.network.InMemoryTokenProvider
import com.example.groceryapp.domain.model.Category
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CategoryRepository(
    private val apiClient: ApiClient = ApiClient(InMemoryTokenProvider.getInstance())
) {
    fun getCategories(): Flow<Result<List<Category>>> = flow {
        try {
            val response = apiClient.client.get("/categories")
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
}
