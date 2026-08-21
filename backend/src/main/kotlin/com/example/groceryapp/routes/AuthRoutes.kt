package com.example.groceryapp.routes

import com.example.groceryapp.models.LoginRequest
import com.example.groceryapp.models.RegisterRequest
import com.example.groceryapp.services.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()
            authService.register(request)
                .onSuccess { call.respond(HttpStatusCode.Created, it) }
                .onFailure { call.respond(HttpStatusCode.BadRequest, it.message ?: "Registration failed") }
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            authService.login(request)
                .onSuccess { call.respond(HttpStatusCode.OK, it) }
                .onFailure { call.respond(HttpStatusCode.Unauthorized, it.message ?: "Login failed") }
        }

        post("/logout") {
            // JWT is stateless. Client simply discards the token.
            call.respond(HttpStatusCode.OK, mapOf("message" to "Logged out successfully"))
        }
    }
}
