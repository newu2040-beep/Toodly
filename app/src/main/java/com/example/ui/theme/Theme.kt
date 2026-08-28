package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class ExtendedColors(
    val customAccent: Color,
    val customAccentLight: Color,
    val customAccentDark: Color,
    val cardBackground: Color,
    val subtleBackground: Color,
    val cardBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        customAccent = PastelLavender,
        customAccentLight = PastelLavenderLight,
        customAccentDark = PastelLavenderDark,
        cardBackground = LightSurface,
        subtleBackground = LightSurfaceSubtle,
        cardBorder = LightBorder,
        textPrimary = LightTextPrimary,
        textSecondary = LightTextSecondary,
        textTertiary = LightTextTertiary
    )
}

val LocalCompactMode = staticCompositionLocalOf { false }

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun MyApplicationTheme(
    themeMode: String = "SYSTEM", // "SYSTEM", "LIGHT", "DARK"
    accentName: String = "Lavender",
    compactMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        "DARK" -> true
        "LIGHT" -> false
        else -> isSystemInDarkTheme()
    }

    val palette = AccentPalettes[accentName] ?: AccentPalettes["Lavender"]!!

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = palette.primary,
            onPrimary = Color.White,
            primaryContainer = palette.primaryDark,
            onPrimaryContainer = Color.White,
            secondary = PastelSkyBlue,
            onSecondary = Color.White,
            background = DarkBackground,
            onBackground = DarkTextPrimary,
            surface = DarkSurface,
            onSurface = DarkTextPrimary,
            surfaceVariant = DarkSurfaceSubtle,
            onSurfaceVariant = DarkTextSecondary,
            outline = DarkBorder
        )
    } else {
        lightColorScheme(
            primary = palette.primary,
            onPrimary = Color.White,
            primaryContainer = palette.primaryLight,
            onPrimaryContainer = palette.primaryDark,
            secondary = PastelSkyBlue,
            onSecondary = Color.White,
            background = LightBackground,
            onBackground = LightTextPrimary,
            surface = LightSurface,
            onSurface = LightTextPrimary,
            surfaceVariant = LightSurfaceSubtle,
            onSurfaceVariant = LightTextSecondary,
            outline = LightBorder
        )
    }

    val extendedColors = if (isDark) {
        ExtendedColors(
            customAccent = palette.primary,
            customAccentLight = palette.primaryDark.copy(alpha = 0.4f),
            customAccentDark = palette.primaryLight,
            cardBackground = DarkSurface,
            subtleBackground = DarkSurfaceSubtle,
            cardBorder = DarkBorder,
            textPrimary = DarkTextPrimary,
            textSecondary = DarkTextSecondary,
            textTertiary = DarkTextTertiary
        )
    } else {
        ExtendedColors(
            customAccent = palette.primary,
            customAccentLight = palette.primaryLight,
            customAccentDark = palette.primaryDark,
            cardBackground = LightSurface,
            subtleBackground = LightSurfaceSubtle,
            cardBorder = LightBorder,
            textPrimary = LightTextPrimary,
            textSecondary = LightTextSecondary,
            textTertiary = LightTextTertiary
        )
    }

    CompositionLocalProvider(
        LocalExtendedColors provides extendedColors,
        LocalCompactMode provides compactMode
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = AppShapes,
            content = content
        )
    }
}
