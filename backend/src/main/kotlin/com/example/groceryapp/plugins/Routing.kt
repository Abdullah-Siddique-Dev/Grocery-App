package com.example.groceryapp.plugins

import com.example.groceryapp.routes.authRoutes
import com.example.groceryapp.routes.cartRoutes
import com.example.groceryapp.routes.categoryRoutes
import com.example.groceryapp.routes.orderRoutes
import com.example.groceryapp.routes.productRoutes
import com.example.groceryapp.routes.userRoutes
import com.example.groceryapp.services.AuthService
import com.example.groceryapp.services.CartService
import com.example.groceryapp.services.CategoryService
import com.example.groceryapp.services.OrderService
import com.example.groceryapp.services.ProductService
import com.example.groceryapp.services.UserService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val jwtSecret = environment.config.property("jwt.secret").getString()
    val jwtIssuer = environment.config.property("jwt.issuer").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    
    val authService = AuthService(
        jwtSecret = jwtSecret,
        jwtIssuer = jwtIssuer,
        jwtAudience = jwtAudience
    )

    routing {
        get("/") {
            call.respondText("Smart Grocery App Backend is running!")
        }

        // Module routes
        authRoutes(authService)
        categoryRoutes()
        productRoutes()
        cartRoutes()
        orderRoutes()
        userRoutes()
    }
}
