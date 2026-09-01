package com.example.groceryapp.routes

import com.example.groceryapp.models.Address
import com.example.groceryapp.models.UserUpdateRequest
import com.example.groceryapp.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(userService: UserService = UserService()) {
    authenticate("auth-jwt") {
        route("/user/profile") {
            get {
                val userId = call.principal<JWTPrincipal>()?.subject ?: return@get call.respond(HttpStatusCode.Unauthorized)
                userService.getUserProfile(userId)
                    .onSuccess { call.respond(it) }
                    .onFailure { call.respond(HttpStatusCode.NotFound, it.message ?: "User not found") }
            }

            put {
                val userId = call.principal<JWTPrincipal>()?.subject ?: return@put call.respond(HttpStatusCode.Unauthorized)
                val request = call.receive<UserUpdateRequest>()
                userService.updateProfile(userId, request)
                    .onSuccess { call.respond(it) }
                    .onFailure { call.respond(HttpStatusCode.BadRequest, it.message ?: "Update failed") }
            }

            put("/address") {
                val userId = call.principal<JWTPrincipal>()?.subject ?: return@put call.respond(HttpStatusCode.Unauthorized)
                val address = call.receive<Address>()
                userService.updateAddress(userId, address)
                    .onSuccess { call.respond(it) }
                    .onFailure { call.respond(HttpStatusCode.BadRequest, it.message ?: "Failed to update address") }
            }

            post("/fcm-token") {
                val userId = call.principal<JWTPrincipal>()?.subject ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val request = call.receive<Map<String, String?>>()
                val token = request["token"]
                userService.updateFcmToken(userId, token)
                    .onSuccess { call.respond(HttpStatusCode.OK, mapOf("message" to "Token updated")) }
                    .onFailure { call.respond(HttpStatusCode.InternalServerError, it.message ?: "Failed to update token") }
            }
        }
    }
}
