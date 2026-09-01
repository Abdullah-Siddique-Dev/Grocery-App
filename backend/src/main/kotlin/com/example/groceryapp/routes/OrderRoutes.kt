package com.example.groceryapp.routes

import com.example.groceryapp.models.*
import com.example.groceryapp.services.OrderService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.orderRoutes(orderService: OrderService = OrderService()) {
    authenticate("auth-jwt") {
        route("/orders") {
            post {
                val userId = call.principal<JWTPrincipal>()?.subject ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val request = call.receive<OrderRequest>()
                orderService.placeOrder(userId, request.deliveryAddress, request.paymentMethod)
                    .onSuccess { call.respond(HttpStatusCode.Created, it) }
                    .onFailure { call.respond(HttpStatusCode.BadRequest, it.message ?: "Failed to place order") }
            }

            get {
                val userId = call.principal<JWTPrincipal>()?.subject ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val orders = orderService.getOrderHistory(userId)
                call.respond(orders)
            }

            get("/{id}") {
                val userId = call.principal<JWTPrincipal>()?.subject ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val orderId = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing order ID")
                orderService.getOrderDetails(userId, orderId)
                    .onSuccess { call.respond(it) }
                    .onFailure { call.respond(HttpStatusCode.NotFound, it.message ?: "Order not found") }
            }

            patch("/{id}/cancel") {
                val userId = call.principal<JWTPrincipal>()?.subject ?: return@patch call.respond(HttpStatusCode.Unauthorized)
                val orderId = call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest, "Missing order ID")
                orderService.cancelOrder(userId, orderId)
                    .onSuccess { call.respond(HttpStatusCode.OK, mapOf("message" to "Order cancelled successfully")) }
                    .onFailure { call.respond(HttpStatusCode.BadRequest, it.message ?: "Failed to cancel order") }
            }

            patch("/{id}/status") {
                val role = call.principal<JWTPrincipal>()?.payload?.getClaim("role")?.asString()
                if (role != UserRole.ADMIN.name) {
                    return@patch call.respond(HttpStatusCode.Forbidden, "Only admins can update order status")
                }

                val orderId = call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest, "Missing order ID")
                val request = call.receive<UpdateStatusRequest>()
                orderService.updateOrderStatus(orderId, request.status)
                    .onSuccess { call.respond(HttpStatusCode.OK, mapOf("message" to "Status updated to ${request.status}")) }
                    .onFailure { call.respond(HttpStatusCode.BadRequest, it.message ?: "Failed to update status") }
            }
        }
    }
}
