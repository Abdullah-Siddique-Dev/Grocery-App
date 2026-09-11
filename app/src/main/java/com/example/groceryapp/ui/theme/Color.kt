package com.example.groceryapp.ui.theme

import androidx.compose.ui.graphics.Color

// Premium Fresh Emerald Palette
val EmeraldPrimary = Color(0xFF16A34A)
val EmeraldDeep = Color(0xFF15803D)
val EmeraldLight = Color(0xFFECFDF3) // Soft Green Surface
val EmeraldOnLight = Color(0xFF065F46)

val AccentLime = Color(0xFF84CC16)
val AccentAmber = Color(0xFFF59E0B)

// Neutral & Backgrounds
val FreshBackground = Color(0xFFF8FAF7)
val White = Color(0xFFFFFFFF)
val SurfaceWhite = Color(0xFFFFFFFF)

// Typography
val TextPrimary = Color(0xFF17221B)
val TextSecondary = Color(0xFF647067)
val TextMuted = Color(0xFF94A39A)

// Status
val StatusError = Color(0xFFDC2626)
val StatusSuccess = Color(0xFF16A34A)
val StatusWarning = Color(0xFFF59E0B)

// Legacy compatibility or extras
val Black = Color(0xFF000000)

// Material 3 Mapping Helpers
val GroceryPrimary = EmeraldPrimary
val GroceryPrimaryDark = EmeraldDeep
val GroceryPrimaryContainer = EmeraldLight
val GroceryOnPrimaryContainer = EmeraldOnLight

val GrocerySecondary = AccentLime
val GrocerySecondaryContainer = EmeraldLight

val GroceryBackground = FreshBackground
val GrocerySurface = SurfaceWhite
val GrocerySurfaceVariant = Color(0xFFF1F5F1)

val GroceryTextPrimary = TextPrimary
val GroceryTextSecondary = TextSecondary
val GroceryTextTertiary = TextMuted

val GroceryError = StatusError
val GrocerySuccess = StatusSuccess
