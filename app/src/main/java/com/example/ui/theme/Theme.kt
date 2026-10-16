package com.example.ui.theme

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
    primary = RongdhonuPrimaryLight,
    onPrimary = Color.Black,
    primaryContainer = RongdhonuPrimaryDark,
    onPrimaryContainer = Color.White,
    secondary = RongdhonuSecondaryLight,
    onSecondary = Color.Black,
    secondaryContainer = RongdhonuSecondaryDark,
    onSecondaryContainer = Color.White,
    tertiary = RongdhonuAccentCyan,
    background = SurfaceDark,
    surface = SurfaceDarkCard,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    outline = BorderLight.copy(alpha = 0.2f)
)

private val LightColorScheme = lightColorScheme(
    primary = RongdhonuPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF2FF),
    onPrimaryContainer = RongdhonuPrimaryDark,
    secondary = RongdhonuSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF7ED),
    onSecondaryContainer = RongdhonuSecondaryDark,
    tertiary = RongdhonuAccentCyan,
    background = SurfaceLight,
    surface = SurfaceCard,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = BorderLight
)

@Composable
fun RongdhonuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded vibrant colors
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
