package com.example.groceryapp.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiClient(private val tokenProvider: TokenProvider) {

    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(Logging) {
            level = LogLevel.ALL
        }

        // Timeout configuration for faster failure detection
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000 // 15 seconds total timeout
            connectTimeoutMillis = 5_000   // 5 seconds to establish connection
            socketTimeoutMillis = 15_000   // 15 seconds to read response
        }

        // Retry failed requests with exponential backoff
        install(HttpRequestRetry) {
            maxRetries = 3
            retryIf { _, response ->
                // Retry on server errors (500-599)
                response.status.value in 500..599
            }
            retryOnExceptionIf { _, cause ->
                // Retry on network errors
                cause is java.io.IOException
            }
            exponentialDelay(base = 2.0, maxDelayMs = 10_000)
        }

        install(Auth) {
            bearer {
                loadTokens {
                    tokenProvider.getToken()?.let {
                        BearerTokens(it, "")
                    }
                }
            }
        }

        defaultRequest {
            url(ApiConfig.BASE_URL)
        }
    }
}
