package com.example.groceryapp.data.network

object ApiConfig {
    // Use 127.0.0.1 with 'adb reverse tcp:8080 tcp:8080' via USB cable
    // This bypasses Wi-Fi/Firewall issues.
    const val BASE_URL = "http://127.0.0.1:8080"
}
