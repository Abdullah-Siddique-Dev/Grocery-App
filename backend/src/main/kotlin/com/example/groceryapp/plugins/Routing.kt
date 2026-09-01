package com.example.groceryapp.plugins

import com.example.groceryapp.routes.*
import com.example.groceryapp.services.*
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
        favoriteRoutes()
        reviewRoutes()
        orderRoutes()
        userRoutes()
        adminRoutes()
    }
}
