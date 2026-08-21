package com.example.groceryapp.routes

import com.example.groceryapp.models.CartItemRequest
import com.example.groceryapp.models.UpdateQuantityRequest
import com.example.groceryapp.services.CartService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.cartRoutes(cartService: CartService = CartService()) {
    authenticate("auth-jwt") {
        route("/cart") {
            get {
                val userId = call.principal<JWTPrincipal>()?.subject ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val cart = cartService.getCart(userId)
                call.respond(cart)
            }

            post("/items") {
                val userId = call.principal<JWTPrincipal>()?.subject ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val request = call.receive<CartItemRequest>()
                cartService.addItem(userId, request.productId, request.quantity)
                    .onSuccess { call.respond(it) }
                    .onFailure { call.respond(HttpStatusCode.BadRequest, it.message ?: "Failed to add item") }
            }

            put("/items/{id}") {
                val userId = call.principal<JWTPrincipal>()?.subject ?: return@put call.respond(HttpStatusCode.Unauthorized)
                val productId = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing product ID")
                val request = call.receive<UpdateQuantityRequest>()
                cartService.updateQuantity(userId, productId, request.quantity)
                    .onSuccess { call.respond(it) }
                    .onFailure { call.respond(HttpStatusCode.BadRequest, it.message ?: "Failed to update quantity") }
            }

            delete("/items/{id}") {
                val userId = call.principal<JWTPrincipal>()?.subject ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                val productId = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing product ID")
                cartService.removeItem(userId, productId)
                    .onSuccess { call.respond(it) }
                    .onFailure { call.respond(HttpStatusCode.BadRequest, it.message ?: "Failed to remove item") }
            }

            delete {
                val userId = call.principal<JWTPrincipal>()?.subject ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                cartService.clearCart(userId)
                    .onSuccess { call.respond(HttpStatusCode.OK, mapOf("message" to "Cart cleared")) }
                    .onFailure { call.respond(HttpStatusCode.InternalServerError, "Failed to clear cart") }
            }
        }
    }
}
