package com.example.groceryapp.services

import com.example.groceryapp.models.OrderStatus
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.slf4j.LoggerFactory

interface NotificationService {
    suspend fun sendOrderStatusNotification(userId: String, orderId: String, newStatus: OrderStatus)
}

class FcmNotificationService(
    private val userService: UserService = UserService()
) : NotificationService {
    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun sendOrderStatusNotification(userId: String, orderId: String, newStatus: OrderStatus) {
        val token = userService.getFcmToken(userId)
        if (token == null) {
            logger.info("No FCM token found for user $userId, skipping notification")
            return
        }

        val (title, body) = when (newStatus) {
            OrderStatus.CONFIRMED -> "Order Confirmed" to "Your order #${orderId.takeLast(6)} has been confirmed."
            OrderStatus.SHIPPED -> "Order Shipped" to "Your order #${orderId.takeLast(6)} is on the way!"
            OrderStatus.DELIVERED -> "Order Delivered" to "Your order #${orderId.takeLast(6)} has been delivered. Enjoy!"
            OrderStatus.CANCELLED -> "Order Cancelled" to "Your order #${orderId.takeLast(6)} has been cancelled."
            else -> return
        }

        try {
            val message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build())
                .putData("orderId", orderId)
                .build()

            val response = FirebaseMessaging.getInstance().sendAsync(message).get()
            logger.info("Successfully sent message to $userId: $response")
        } catch (e: Exception) {
            logger.error("Failed to send FCM notification for order $orderId to user $userId", e)
        }
    }
}
