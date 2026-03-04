package com.example.langmap.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Blue,
    onPrimary = BackgroundLight,
    primaryContainer = BlueLight,
    secondary = Gray500,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = Red,
    outline = Gray300,
)

private val DarkColorScheme = darkColorScheme(
    primary = BlueDark,
    onPrimary = BackgroundLight,
    primaryContainer = Blue,
    secondary = Gray500,
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    error = Red,
    outline = Gray600,
)

@Composable
fun LangMapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}