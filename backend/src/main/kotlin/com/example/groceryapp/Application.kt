package com.example.groceryapp

import com.example.groceryapp.plugins.*
import com.example.groceryapp.database.AppDatabase
import com.example.groceryapp.database.SeedData
import io.ktor.server.application.*
import io.ktor.server.netty.*
import kotlinx.coroutines.launch

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
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
