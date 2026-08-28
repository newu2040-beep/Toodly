package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Pastel Palettes
val PastelLavender = Color(0xFF7C68EE)
val PastelLavenderLight = Color(0xFFF3EFFF)
val PastelLavenderDark = Color(0xFF5A46DE)

val PastelMint = Color(0xFF27AE60)
val PastelMintLight = Color(0xFFE8F8F0)
val PastelMintDark = Color(0xFF1E8A4C)

val PastelPink = Color(0xFFFF6584)
val PastelPinkLight = Color(0xFFFFEBF0)
val PastelPinkDark = Color(0xFFE04566)

val PastelPeach = Color(0xFFF2994A)
val PastelPeachLight = Color(0xFFFFF3E8)
val PastelPeachDark = Color(0xFFD67B29)

val PastelSkyBlue = Color(0xFF3B82F6)
val PastelSkyBlueLight = Color(0xFFEBF3FE)
val PastelSkyBlueDark = Color(0xFF2563EB)

val PastelYellow = Color(0xFFF59E0B)
val PastelYellowLight = Color(0xFFFEF9E8)

// Light Backgrounds & Surfaces
val LightBackground = Color(0xFFF7F8FC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceSubtle = Color(0xFFF0F2FA)
val LightBorder = Color(0xFFECEEF8)
val LightTextPrimary = Color(0xFF191C24)
val LightTextSecondary = Color(0xFF7A8094)
val LightTextTertiary = Color(0xFFA2A8BC)

// Dark Backgrounds & Surfaces
val DarkBackground = Color(0xFF11121A)
val DarkSurface = Color(0xFF1A1C28)
val DarkSurfaceSubtle = Color(0xFF232637)
val DarkBorder = Color(0xFF2D3045)
val DarkTextPrimary = Color(0xFFF4F6FC)
val DarkTextSecondary = Color(0xFFA0A6BC)
val DarkTextTertiary = Color(0xFF6B7288)

// Status colors
val PriorityHigh = Color(0xFFFF4D4D)
val PriorityHighBg = Color(0xFFFFECEC)
val PriorityMedium = Color(0xFFF59E0B)
val PriorityMediumBg = Color(0xFFFEF9E8)
val PriorityLow = Color(0xFF3B82F6)
val PriorityLowBg = Color(0xFFEBF3FE)

data class AccentPalette(
    val name: String,
    val primary: Color,
    val primaryLight: Color,
    val primaryDark: Color
)

val AccentPalettes = mapOf(
    "Lavender" to AccentPalette("Lavender", PastelLavender, PastelLavenderLight, PastelLavenderDark),
    "Mint" to AccentPalette("Mint", PastelMint, PastelMintLight, PastelMintDark),
    "Pink" to AccentPalette("Pink", PastelPink, PastelPinkLight, PastelPinkDark),
    "Peach" to AccentPalette("Peach", PastelPeach, PastelPeachLight, PastelPeachDark),
    "SkyBlue" to AccentPalette("SkyBlue", PastelSkyBlue, PastelSkyBlueLight, PastelSkyBlueDark)
)
