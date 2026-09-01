package com.example.groceryapp.plugins

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.ktor.server.application.*
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream

fun Application.configureFirebase() {
    val logger = LoggerFactory.getLogger("FirebasePlugin")
    val serviceAccountPath = environment.config.propertyOrNull("firebase.serviceAccountPath")?.getString() ?: "firebase-service-account.json"
    
    val serviceAccountFile = File(serviceAccountPath)
    
    if (!serviceAccountFile.exists()) {
        logger.warn("Firebase service account file not found at $serviceAccountPath. Push notifications will be disabled.")
        return
    }

    try {
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(FileInputStream(serviceAccountFile)))
            .build()

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options)
            logger.info("Firebase Admin SDK initialized successfully.")
        }
    } catch (e: Exception) {
        logger.error("Failed to initialize Firebase Admin SDK", e)
    }
}
