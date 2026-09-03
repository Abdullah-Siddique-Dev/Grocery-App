package com.example.groceryapp.data.repository

import com.example.groceryapp.data.dto.*
import com.example.groceryapp.data.network.ApiClient
import com.example.groceryapp.data.network.InMemoryTokenProvider
import com.example.groceryapp.domain.model.ProductReviews
import com.example.groceryapp.domain.model.Review
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ReviewRepository(
    private val apiClient: ApiClient = ApiClient(InMemoryTokenProvider.getInstance())
) {
    
    fun getProductReviews(productId: String): Flow<Result<ProductReviews>> = flow {
        try {
            val response = apiClient.client.get("/products/$productId/reviews")
            if (response.status.value in 200..299) {
                val dto = response.body<ProductReviewsResponseDto>()
                emit(Result.success(dto.toDomain()))
            } else {
                emit(Result.failure(Exception("Failed to fetch reviews: ${response.status}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun addReview(productId: String, rating: Int, comment: String): Result<Review> {
        return try {
            val response = apiClient.client.post("/products/$productId/reviews") {
                contentType(ContentType.Application.Json)
                setBody(ReviewRequestDto(rating, comment))
            }
            if (response.status.value in 200..299) {
                Result.success(response.body<ReviewDto>().toDomain())
            } else {
                Result.failure(Exception("Failed to add review: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateReview(productId: String, reviewId: String, rating: Int, comment: String): Result<Review> {
        return try {
            val response = apiClient.client.put("/products/$productId/reviews/$reviewId") {
                contentType(ContentType.Application.Json)
                setBody(ReviewRequestDto(rating, comment))
            }
            if (response.status.value in 200..299) {
                Result.success(response.body<ReviewDto>().toDomain())
            } else {
                Result.failure(Exception("Failed to update review: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReview(productId: String, reviewId: String): Result<Unit> {
        return try {
            val response = apiClient.client.delete("/products/$productId/reviews/$reviewId")
            if (response.status.value in 200..299) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete review: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
