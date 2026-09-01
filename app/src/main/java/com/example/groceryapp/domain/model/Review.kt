package com.example.groceryapp.domain.model

data class Review(
    val id: String,
    val productId: String,
    val userId: String,
    val userName: String,
    val rating: Int,
    val comment: String,
    val createdAt: String
)

data class ReviewSummary(
    val averageRating: Double,
    val totalReviews: Int
)

data class ProductReviews(
    val summary: ReviewSummary,
    val reviews: List<Review>
)
