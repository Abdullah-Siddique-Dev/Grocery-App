package com.example.groceryapp.config

import io.ktor.server.application.*

class AppConfig(application: Application) {
    val jwtSecret = application.environment.config.property("jwt.secret").getString()
    val jwtIssuer = application.environment.config.property("jwt.issuer").getString()
    val jwtAudience = application.environment.config.property("jwt.audience").getString()
    
    val mongoUri = application.environment.config.property("mongodb.uri").getString()
    val mongoDatabase = application.environment.config.property("mongodb.database").getString()
}
