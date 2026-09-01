package com.example.groceryapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewDto(
    @SerialName("_id")
    val id: String?,
    val productId: String,
    val userId: String,
    val userName: String,
    val rating: Int,
    val comment: String,
    val createdAt: String
)

@Serializable
data class ReviewSummaryDto(
    val averageRating: Double,
    val totalReviews: Int
)

@Serializable
data class ProductReviewsResponseDto(
    val summary: ReviewSummaryDto,
    val reviews: List<ReviewDto>
)

@Serializable
data class ReviewRequestDto(
    val rating: Int,
    val comment: String
)
