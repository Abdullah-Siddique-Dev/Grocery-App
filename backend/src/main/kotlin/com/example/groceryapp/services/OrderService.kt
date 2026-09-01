package com.example.groceryapp.services

import com.example.groceryapp.models.*
import com.example.groceryapp.repositories.CartRepository
import com.example.groceryapp.repositories.OrderRepository
import com.example.groceryapp.repositories.ProductRepository
import java.time.Instant

class OrderService(
    private val orderRepository: OrderRepository = OrderRepository(),
    private val cartRepository: CartRepository = CartRepository(),
    private val productRepository: ProductRepository = ProductRepository(),
    private val notificationService: NotificationService = FcmNotificationService()
) {

    suspend fun placeOrder(userId: String, deliveryAddress: Address, paymentMethod: PaymentMethod): Result<Order> {
        val cart = cartRepository.getCartByUserId(userId)
            ?: return Result.failure(Exception("Cart not found"))

        if (cart.items.isEmpty()) {
            return Result.failure(Exception("Cannot place an order with an empty cart"))
        }

        // Atomic Stock Reduction and Price Verification
        val processedItems = mutableListOf<Pair<String, Int>>()
        val orderItems = mutableListOf<OrderItem>()
        
        for (item in cart.items) {
            val product = productRepository.findById(item.productId)
                ?: return Result.failure(Exception("Product not found: ${item.productId}"))
            
            val success = productRepository.decrementStock(item.productId, item.quantity)
            if (!success) {
                // Rollback
                processedItems.forEach { (pid, qty) -> productRepository.incrementStock(pid, qty) }
                return Result.failure(Exception("Insufficient stock for product: ${product.name}"))
            }
            processedItems.add(item.productId to item.quantity)
            
            orderItems.add(OrderItem(
                productId = item.productId,
                quantity = item.quantity,
                price = product.price // Use current trusted price from DB
            ))
        }

        val totalAmount = orderItems.sumOf { it.price * it.quantity }

        val order = Order(
            userId = userId,
            items = orderItems,
            totalAmount = totalAmount,
            deliveryAddress = deliveryAddress,
            status = OrderStatus.PENDING,
            paymentMethod = paymentMethod,
            paymentStatus = PaymentStatus.PENDING,
            placedAt = Instant.now().toString()
        )

        return try {
            val createdOrder = orderRepository.create(order)
            // Clear cart after successful order creation
            cartRepository.deleteCart(userId)
            Result.success(createdOrder)
        } catch (e: Exception) {
            // Rollback stock if order creation fails
            processedItems.forEach { (pid, qty) -> productRepository.incrementStock(pid, qty) }
            Result.failure(e)
        }
    }

    suspend fun getOrderHistory(userId: String): List<Order> {
        return orderRepository.findAllByUserId(userId)
    }

    suspend fun getAllOrders(): List<Order> {
        return orderRepository.findAll()
    }

    suspend fun getOrderDetails(userId: String, orderId: String): Result<Order> {
        val order = orderRepository.findById(orderId, userId)
            ?: return Result.failure(Exception("Order not found or unauthorized"))
        return Result.success(order)
    }

    suspend fun cancelOrder(userId: String, orderId: String): Result<Boolean> {
        val order = orderRepository.findById(orderId, userId)
            ?: return Result.failure(Exception("Order not found"))

        if (order.status != OrderStatus.PENDING && order.status != OrderStatus.CONFIRMED) {
            return Result.failure(Exception("Order cannot be cancelled in its current state: ${order.status}"))
        }

        val success = orderRepository.updateStatus(orderId, OrderStatus.CANCELLED)
        if (success) {
            // Restore Stock
            order.items.forEach { item ->
                productRepository.incrementStock(item.productId, item.quantity)
            }
            // Send Notification
            notificationService.sendOrderStatusNotification(userId, orderId, OrderStatus.CANCELLED)
            return Result.success(true)
        }
        return Result.failure(Exception("Failed to cancel order"))
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Boolean> {
        val order = orderRepository.findById(orderId)
            ?: return Result.failure(Exception("Order not found"))

        if (!isValidTransition(order.status, newStatus)) {
            return Result.failure(Exception("Invalid status transition from ${order.status} to $newStatus"))
        }

        val success = orderRepository.updateStatus(orderId, newStatus)
        if (success) {
            // Send Notification
            notificationService.sendOrderStatusNotification(order.userId, orderId, newStatus)
            return Result.success(true)
        }
        return Result.failure(Exception("Failed to update status"))
    }

    private fun isValidTransition(current: OrderStatus, next: OrderStatus): Boolean {
        return when (current) {
            OrderStatus.PENDING -> next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED
            OrderStatus.CONFIRMED -> next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED
            OrderStatus.SHIPPED -> next == OrderStatus.DELIVERED
            OrderStatus.DELIVERED -> false
            OrderStatus.CANCELLED -> false
        }
    }
}
