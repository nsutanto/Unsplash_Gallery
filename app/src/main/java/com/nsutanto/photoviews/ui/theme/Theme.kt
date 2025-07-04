package com.nsutanto.photoviews.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1E88E5), // A bright blue for the primary color
    onPrimary = Color.White, // Text color on primary
    secondary = Color(0xFF26C6DA), // A teal for the secondary color
    onSecondary = Color.Black, // Text color on secondary
    background = Color(0xFF121212), // Dark background for dark mode
    onBackground = Color.White, // Text color for dark background
    surface = Color(0xFF1E1E1E), // Dark surface for cards and surfaces
    onSurface = Color.White, // Text color on surface
    error = Color(0xFFCF6679), // Standard error color
    onError = Color.White, // Text color on error surfaces
    tertiary = Color(0xFF3700B3) // A purple accent color
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE), // A deep purple for the primary color
    onPrimary = Color.White, // Text color on primary
    secondary = Color(0xFF03DAC6), // A bright teal for the secondary color
    onSecondary = Color.Black, // Text color on secondary
    background = Color(0xFFFFFFFF), // Light background for light mode
    onBackground = Color.Black, // Text color for light background
    surface = Color(0xFFFAFAFA), // Light surface color for cards and surfaces
    onSurface = Color.Black, // Text color on surface
    error = Color(0xFFB00020), // Red for errors
    onError = Color.White, // Text color on error surfaces
    tertiary = Color(0xFF018786) // A secondary teal color for tertiary elements
)

@Composable
fun PhotoViewsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}