package com.example.groceryapp.routes

import com.example.groceryapp.services.FavoriteService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.favoriteRoutes(favoriteService: FavoriteService = FavoriteService()) {
    authenticate("auth-jwt") {
        route("/favorites") {
            get {
                val userId = call.principal<JWTPrincipal>()?.subject ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val favorites = favoriteService.getFavorites(userId)
                call.respond(favorites)
            }

            post("/{productId}") {
                val userId = call.principal<JWTPrincipal>()?.subject ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val productId = call.parameters["productId"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing product ID")
                
                favoriteService.addFavorite(userId, productId)
                    .onSuccess { call.respond(HttpStatusCode.Created, mapOf("message" to "Product added to favorites")) }
                    .onFailure { call.respond(HttpStatusCode.BadRequest, it.message ?: "Failed to add favorite") }
            }

            delete("/{productId}") {
                val userId = call.principal<JWTPrincipal>()?.subject ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                val productId = call.parameters["productId"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing product ID")
                
                favoriteService.removeFavorite(userId, productId)
                    .onSuccess { call.respond(HttpStatusCode.OK, mapOf("message" to "Product removed from favorites")) }
                    .onFailure { call.respond(HttpStatusCode.NotFound, it.message ?: "Favorite not found") }
            }

            get("/check/{productId}") {
                val userId = call.principal<JWTPrincipal>()?.subject ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val productId = call.parameters["productId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing product ID")
                
                val isFavorited = favoriteService.isFavorited(userId, productId)
                call.respond(mapOf("isFavorited" to isFavorited))
            }
        }
    }
}
