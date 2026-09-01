package com.example.groceryapp.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
enum class UserRole {
    CUSTOMER,
    ADMIN
}

@Serializable
data class Address(
    val fullName: String,
    val phoneNumber: String,
    val addressLine: String,
    val city: String,
    val postalCode: String
)

@Serializable
data class User(
    @SerialName("_id")
    val id: String? = null,
    val name: String,
    val email: String,
    val passwordHash: String,
    val phoneNumber: String,
    val address: Address? = null,
    val role: UserRole = UserRole.CUSTOMER,
    val fcmToken: String? = null,
    val createdAt: String
)

@Serializable
data class Category(
    @SerialName("_id")
    val id: String? = null,
    val name: String,
    val icon: String,
    val imageUrl: String,
    val displayOrder: Int
)

@Serializable
data class Product(
    @SerialName("_id")
    val id: String? = null,
    val name: String,
    val description: String,
    val categoryId: String,
    val price: Double,
    val unit: String,
    val imageUrl: String,
    val stockQuantity: Int,
    val isAvailable: Boolean,
    val createdAt: String
)

@Serializable
data class Cart(
    @SerialName("_id")
    val id: String? = null,
    val userId: String,
    val items: List<CartItem>,
    val updatedAt: String
)

@Serializable
data class CartItem(
    val productId: String,
    val quantity: Int,
    val priceAtAdd: Double
)

@Serializable
enum class OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}

@Serializable
enum class PaymentMethod {
    CASH_ON_DELIVERY,
    ONLINE
}

@Serializable
enum class PaymentStatus {
    PENDING,
    PAID,
    FAILED,
    REFUNDED
}

@Serializable
data class Order(
    @SerialName("_id")
    val id: String? = null,
    val userId: String,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val deliveryAddress: Address,
    val status: OrderStatus = OrderStatus.PENDING,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH_ON_DELIVERY,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val placedAt: String
)

@Serializable
data class OrderItem(
    val productId: String,
    val quantity: Int,
    val price: Double
)

@Serializable
data class OrderRequest(
    val deliveryAddress: Address,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH_ON_DELIVERY
)

@Serializable
data class UpdateStatusRequest(val status: OrderStatus)

@Serializable
data class Favorite(
    @SerialName("_id")
    val id: String? = null,
    val userId: String,
    val productId: String,
    val createdAt: String
)

@Serializable
data class Review(
    @SerialName("_id")
    val id: String? = null,
    val productId: String,
    val userId: String,
    val userName: String,
    val rating: Int,
    val comment: String,
    val createdAt: String
)

@Serializable
data class ReviewSummary(
    val averageRating: Double,
    val totalReviews: Int
)

@Serializable
data class ProductReviewsResponse(
    val summary: ReviewSummary,
    val reviews: List<Review>
)
