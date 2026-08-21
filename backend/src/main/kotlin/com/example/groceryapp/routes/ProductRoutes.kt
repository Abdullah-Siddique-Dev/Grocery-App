package com.example.groceryapp.routes

import com.example.groceryapp.services.ProductService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.productRoutes(productService: ProductService = ProductService()) {
    authenticate("auth-jwt") {
        get("/products") {
            val categoryId = call.request.queryParameters["categoryId"]
            val query = call.request.queryParameters["q"]
            
            val products = productService.getProducts(categoryId, query)
            call.respond(products)
        }

        get("/products/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing product ID")
            val product = productService.getProductById(id)
            if (product != null) {
                call.respond(product)
            } else {
                call.respond(HttpStatusCode.NotFound, "Product not found")
            }
        }
    }
}
