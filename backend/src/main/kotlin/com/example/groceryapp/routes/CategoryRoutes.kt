package com.example.groceryapp.routes

import com.example.groceryapp.services.CategoryService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.categoryRoutes(categoryService: CategoryService = CategoryService()) {
    authenticate("auth-jwt") {
        get("/categories") {
            val categories = categoryService.getAllCategories()
            call.respond(categories)
        }
    }
}
