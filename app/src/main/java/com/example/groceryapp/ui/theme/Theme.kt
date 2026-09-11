package com.example.groceryapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = White,
    primaryContainer = EmeraldDeep,
    onPrimaryContainer = White,
    secondary = AccentLime,
    onSecondary = White,
    background = Color(0xFF0F1712), // Darker fresh background
    surface = Color(0xFF17221B),
    onBackground = White,
    onSurface = White,
    error = StatusError
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = White,
    primaryContainer = EmeraldLight,
    onPrimaryContainer = EmeraldOnLight,
    secondary = AccentLime,
    onSecondary = White,
    secondaryContainer = EmeraldLight,
    onSecondaryContainer = EmeraldOnLight,
    tertiary = AccentAmber,
    onTertiary = White,
    background = FreshBackground,
    surface = SurfaceWhite,
    surfaceVariant = GrocerySurfaceVariant,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error = StatusError,
    outline = TextMuted
)

@Composable
fun SmartGroceryAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Stick to Light for that "Fresh" feel unless dark is explicitly requested or preferred
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
