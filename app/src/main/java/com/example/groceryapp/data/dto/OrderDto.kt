package com.example.groceryapp.data.dto

import com.example.groceryapp.domain.model.OrderStatus
import com.example.groceryapp.domain.model.PaymentMethod
import com.example.groceryapp.domain.model.PaymentStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    @SerialName("_id")
    val id: String? = null,
    val userId: String = "",
    val items: List<OrderItemDto> = emptyList(),
    val totalAmount: Double = 0.0,
    val deliveryAddress: AddressDto? = null,
    val status: OrderStatus = OrderStatus.PENDING,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH_ON_DELIVERY,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val placedAt: String = ""
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
