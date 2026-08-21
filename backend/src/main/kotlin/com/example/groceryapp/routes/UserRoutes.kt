package com.example.groceryapp.routes

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
        }
    }
}
