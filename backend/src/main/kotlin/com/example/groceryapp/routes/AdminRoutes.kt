package com.example.groceryapp.routes

import com.example.groceryapp.models.*
import com.example.groceryapp.services.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.Instant

fun Route.adminRoutes(
    orderService: OrderService = OrderService(),
    productService: ProductService = ProductService(),
    categoryService: CategoryService = CategoryService(),
    userService: UserService = UserService()
) {
    authenticate("auth-jwt") {
        route("/admin") {
            // Reusable Admin Check
            intercept(ApplicationCallPipeline.Call) {
                val role = call.principal<JWTPrincipal>()?.payload?.getClaim("role")?.asString()
                if (role != UserRole.ADMIN.name) {
                    call.respond(HttpStatusCode.Forbidden, "Admin access required")
                    finish()
                }
            }

            // Orders
            route("/orders") {
                get {
                    call.respond(orderService.getAllOrders())
                }
                patch("/{id}/status") {
                    val id = call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest)
                    val request = call.receive<UpdateStatusRequest>()
                    orderService.updateOrderStatus(id, request.status)
                        .onSuccess { call.respond(HttpStatusCode.OK, mapOf("message" to "Status updated")) }
                        .onFailure { call.respond(HttpStatusCode.BadRequest, it.message ?: "Update failed") }
                }
            }

            // Products
            route("/products") {
                get {
                    call.respond(productService.getProducts())
                }
                post {
                    val product = call.receive<Product>().copy(createdAt = Instant.now().toString())
                    productService.createProduct(product)
                        .onSuccess { call.respond(HttpStatusCode.Created, it) }
                        .onFailure { call.respond(HttpStatusCode.BadRequest, it.message ?: "Failed to create product") }
                }
                patch("/{id}") {
                    val id = call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest)
                    val product = call.receive<Product>()
                    productService.updateProduct(id, product)
                        .onSuccess { call.respond(it) }
                        .onFailure { call.respond(HttpStatusCode.BadRequest, it.message ?: "Failed to update product") }
                }
                delete("/{id}") {
                    val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                    productService.deleteProduct(id)
                        .onSuccess { call.respond(HttpStatusCode.OK, mapOf("message" to "Product deleted")) }
                        .onFailure { call.respond(HttpStatusCode.BadRequest, it.message ?: "Failed to delete product") }
                }
            }

            // Categories
            route("/categories") {
                get {
                    call.respond(categoryService.getAllCategories())
                }
                post {
                    val category = call.receive<Category>()
                    categoryService.createCategory(category)
                        .onSuccess { call.respond(HttpStatusCode.Created, it) }
                        .onFailure { call.respond(HttpStatusCode.BadRequest, it.message ?: "Failed to create category") }
                }
                patch("/{id}") {
                    val id = call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest)
                    val category = call.receive<Category>()
                    categoryService.updateCategory(id, category)
                        .onSuccess { call.respond(it) }
                        .onFailure { call.respond(HttpStatusCode.BadRequest, it.message ?: "Failed to update category") }
                }
                delete("/{id}") {
                    val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                    categoryService.deleteCategory(id)
                        .onSuccess { call.respond(HttpStatusCode.OK, mapOf("message" to "Category deleted")) }
                        .onFailure { call.respond(HttpStatusCode.BadRequest, it.message ?: "Failed to delete category") }
                }
            }

            // Users
            get("/users") {
                call.respond(userService.getAllUsers())
            }
        }
    }
}
