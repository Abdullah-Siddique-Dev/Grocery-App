package com.example.groceryapp.data.dto

import com.example.groceryapp.domain.model.OrderStatus
import com.example.groceryapp.domain.model.PaymentMethod
import com.example.groceryapp.domain.model.PaymentStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    @SerialName("_id")
    val id: String?,
    val userId: String,
    val items: List<OrderItemDto>,
    val totalAmount: Double,
    val deliveryAddress: AddressDto,
    val status: OrderStatus,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH_ON_DELIVERY,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val placedAt: String
)

@Serializable
data class OrderItemDto(
    val productId: String,
    val quantity: Int,
    val price: Double
)

@Serializable
data class OrderRequestDto(
    val deliveryAddress: AddressDto,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH_ON_DELIVERY
)
