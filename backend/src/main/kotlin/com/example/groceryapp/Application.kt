package com.example.groceryapp

import com.example.groceryapp.plugins.*
import com.example.groceryapp.database.AppDatabase
import com.example.groceryapp.database.SeedData
import io.ktor.server.application.*
import io.ktor.server.netty.*
import kotlinx.coroutines.launch

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    // Validate required configuration
    validateRequiredConfig()
    
    configureFirebase()
    AppDatabase.init(this)
    
    // Seed dummy data
    launch {
        SeedData.seedIfNeeded()
    }

    configureSerialization()
    configureSecurity()
    configureStatusPages()
    configureRouting()
}

private fun Application.validateRequiredConfig() {
    val requiredConfigs = listOf(
        "mongodb.uri" to "MONGODB_URI",
        "jwt.secret" to "JWT_SECRET"
    )
    
    val missingConfigs = mutableListOf<String>()
    
    requiredConfigs.forEach { (configPath, envVar) ->
        try {
            val value = environment.config.propertyOrNull(configPath)?.getString()
            if (value.isNullOrBlank()) {
                missingConfigs.add("$configPath (environment variable: $envVar)")
            }
        } catch (e: Exception) {
            missingConfigs.add("$configPath (environment variable: $envVar)")
        }
    }
    
    if (missingConfigs.isNotEmpty()) {
        val errorMessage = buildString {
            appendLine("ERROR: Missing required configuration!")
            appendLine("The following environment variables must be set:")
            missingConfigs.forEach { appendLine("  - $it") }
            appendLine()
            appendLine("Example:")
            appendLine("  export MONGODB_URI=\"mongodb+srv://username:password@cluster.mongodb.net/\"")
            appendLine("  export JWT_SECRET=\"your-secret-key-here\"")
        }
        throw IllegalStateException(errorMessage)
    }
    
    log.info("Configuration validation passed: All required environment variables are set")
}
