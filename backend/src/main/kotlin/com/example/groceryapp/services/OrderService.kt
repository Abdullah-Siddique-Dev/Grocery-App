package com.example.groceryapp.services

import com.example.groceryapp.models.Order
import com.example.groceryapp.models.OrderItem
import com.example.groceryapp.repositories.CartRepository
import com.example.groceryapp.repositories.OrderRepository
import java.time.Instant

class OrderService(
    private val orderRepository: OrderRepository = OrderRepository(),
    private val cartRepository: CartRepository = CartRepository()
) {

    suspend fun placeOrder(userId: String, deliveryAddress: String): Result<Order> {
        val cart = cartRepository.getCartByUserId(userId)
            ?: return Result.failure(Exception("Cart not found"))

        if (cart.items.isEmpty()) {
            return Result.failure(Exception("Cannot place an order with an empty cart"))
        }

        val orderItems = cart.items.map { cartItem ->
            OrderItem(
                productId = cartItem.productId,
                quantity = cartItem.quantity,
                price = cartItem.priceAtAdd
            )
        }

        val totalAmount = orderItems.sumOf { it.price * it.quantity }

        val order = Order(
            userId = userId,
            items = orderItems,
            totalAmount = totalAmount,
            deliveryAddress = deliveryAddress,
            status = "Pending",
            placedAt = Instant.now().toString()
        )

        return try {
            val createdOrder = orderRepository.create(order)
            // Clear cart after successful order creation
            cartRepository.deleteCart(userId)
            Result.success(createdOrder)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrderHistory(userId: String): List<Order> {
        return orderRepository.findAllByUserId(userId)
    }

    suspend fun getOrderDetails(userId: String, orderId: String): Result<Order> {
        val order = orderRepository.findById(userId, orderId)
            ?: return Result.failure(Exception("Order not found or unauthorized"))
        return Result.success(order)
    }
}
