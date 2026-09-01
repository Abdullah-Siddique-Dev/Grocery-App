package com.example.groceryapp.routes

import com.example.groceryapp.services.ReviewService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class ReviewRequest(
    val rating: Int,
    val comment: String
)

fun Route.reviewRoutes(reviewService: ReviewService = ReviewService()) {
    route("/products/{productId}/reviews") {
        get {
            val productId = call.parameters["productId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing product ID")
            val response = reviewService.getProductReviews(productId)
            call.respond(response)
        }

        authenticate("auth-jwt") {
            post {
                val userId = call.principal<JWTPrincipal>()?.subject ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val productId = call.parameters["productId"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing product ID")
                val request = call.receive<ReviewRequest>()
                
                reviewService.addReview(userId, productId, request.rating, request.comment)
                    .onSuccess { call.respond(HttpStatusCode.Created, it) }
                    .onFailure { call.respond(HttpStatusCode.BadRequest, it.message ?: "Failed to add review") }
            }

            put("/{reviewId}") {
                val userId = call.principal<JWTPrincipal>()?.subject ?: return@put call.respond(HttpStatusCode.Unauthorized)
                val reviewId = call.parameters["reviewId"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing review ID")
                val request = call.receive<ReviewRequest>()
                
                reviewService.updateReview(userId, reviewId, request.rating, request.comment)
                    .onSuccess { call.respond(it) }
                    .onFailure { call.respond(HttpStatusCode.BadRequest, it.message ?: "Failed to update review") }
            }

            delete("/{reviewId}") {
                val userId = call.principal<JWTPrincipal>()?.subject ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                val reviewId = call.parameters["reviewId"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing review ID")
                
                reviewService.deleteReview(userId, reviewId)
                    .onSuccess { call.respond(HttpStatusCode.OK, mapOf("message" to "Review deleted")) }
                    .onFailure { call.respond(HttpStatusCode.BadRequest, it.message ?: "Failed to delete review") }
            }
        }
    }
}
