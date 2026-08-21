package com.example.groceryapp.routes

import com.example.groceryapp.models.OrderRequest
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
                orderService.placeOrder(userId, request.deliveryAddress)
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
        }
    }
}
