package com.example.groceryapp.services

import com.example.groceryapp.models.*
import com.example.groceryapp.repositories.OrderRepository
import com.example.groceryapp.repositories.ReviewRepository
import com.example.groceryapp.repositories.UserRepository
import java.time.Instant

class ReviewService(
    private val reviewRepository: ReviewRepository = ReviewRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val orderRepository: OrderRepository = OrderRepository()
) {

    suspend fun getProductReviews(productId: String): ProductReviewsResponse {
        val reviews = reviewRepository.findAllByProductId(productId)
        val averageRating = if (reviews.isEmpty()) 0.0 else reviews.map { it.rating }.average()
        
        return ProductReviewsResponse(
            summary = ReviewSummary(averageRating, reviews.size),
            reviews = reviews
        )
    }

    suspend fun addReview(userId: String, productId: String, rating: Int, comment: String): Result<Review> {
        if (rating !in 1..5) return Result.failure(Exception("Rating must be between 1 and 5"))
        if (comment.isBlank()) return Result.failure(Exception("Comment cannot be empty"))

        // Check verified purchase (must be delivered)
        val userOrders = orderRepository.findAllByUserId(userId)
        val hasPurchased = userOrders.any { order -> 
            order.status == OrderStatus.DELIVERED && order.items.any { it.productId == productId } 
        }
        
        if (!hasPurchased) {
            return Result.failure(Exception("You can only review products you have purchased"))
        }

        // Check if user already reviewed
        val existing = reviewRepository.findByUserAndProduct(userId, productId)
        if (existing != null) {
            return Result.failure(Exception("You have already reviewed this product"))
        }

        val user = userRepository.findById(userId) ?: return Result.failure(Exception("User not found"))

        val review = Review(
            productId = productId,
            userId = userId,
            userName = user.name,
            rating = rating,
            comment = comment,
            createdAt = Instant.now().toString()
        )

        return try {
            val created = reviewRepository.create(review)
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateReview(userId: String, reviewId: String, rating: Int, comment: String): Result<Review> {
        if (rating !in 1..5) return Result.failure(Exception("Rating must be between 1 and 5"))
        
        val review = reviewRepository.findById(reviewId) ?: return Result.failure(Exception("Review not found"))
        if (review.userId != userId) return Result.failure(Exception("Unauthorized"))

        return try {
            val updated = reviewRepository.update(reviewId, rating, comment)
            if (updated != null) Result.success(updated)
            else Result.failure(Exception("Failed to update review"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReview(userId: String, reviewId: String): Result<Unit> {
        val review = reviewRepository.findById(reviewId) ?: return Result.failure(Exception("Review not found"))
        if (review.userId != userId) return Result.failure(Exception("Unauthorized"))

        return try {
            val deleted = reviewRepository.delete(reviewId)
            if (deleted) Result.success(Unit)
            else Result.failure(Exception("Failed to delete review"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
