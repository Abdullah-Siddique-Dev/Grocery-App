package com.example.groceryapp

import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun module(application: Application) {
    // AppDatabase.init(application)
}
