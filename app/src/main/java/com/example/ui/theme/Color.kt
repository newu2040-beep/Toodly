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

// New Additional Aesthetic Themes
val PastelMatcha = Color(0xFF4E8752)
val PastelMatchaLight = Color(0xFFEDF5EE)
val PastelMatchaDark = Color(0xFF38663C)

val PastelSakura = Color(0xFFE85D88)
val PastelSakuraLight = Color(0xFFFDEEF3)
val PastelSakuraDark = Color(0xFFC43A64)

val PastelOcean = Color(0xFF0288D1)
val PastelOceanLight = Color(0xFFE1F5FE)
val PastelOceanDark = Color(0xFF01579B)

val PastelMocha = Color(0xFF8D6E63)
val PastelMochaLight = Color(0xFFF5EBE6)
val PastelMochaDark = Color(0xFF5D4037)

val PastelTwilight = Color(0xFF5C6BC0)
val PastelTwilightLight = Color(0xFFEDE7F6)
val PastelTwilightDark = Color(0xFF3949AB)

val PastelBerry = Color(0xFFD81B60)
val PastelBerryLight = Color(0xFFFCE4EC)
val PastelBerryDark = Color(0xFFAD1457)

val PastelForest = Color(0xFF2E7D32)
val PastelForestLight = Color(0xFFE8F5E9)
val PastelForestDark = Color(0xFF1B5E20)

val PastelAmber = Color(0xFFE65100)
val PastelAmberLight = Color(0xFFFFF3E0)
val PastelAmberDark = Color(0xFFBF360C)

// Light Backgrounds & Surfaces
val LightBackground = Color(0xFFF7F8FC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceSubtle = Color(0xFFF0F2FA)
val LightBorder = Color(0xFFECEEF8)
val LightTextPrimary = Color(0xFF191C24)
val LightTextSecondary = Color(0xFF7A8094)
val LightTextTertiary = Color(0xFFA2A8BC)

// Warm Cream Backgrounds & Surfaces
val CreamBackground = Color(0xFFFAF6EE)
val CreamSurface = Color(0xFFFFFDF9)
val CreamSurfaceSubtle = Color(0xFFF2ECE0)
val CreamBorder = Color(0xFFE8E0D2)
val CreamTextPrimary = Color(0xFF2B2620)
val CreamTextSecondary = Color(0xFF7D756B)
val CreamTextTertiary = Color(0xFFA89F93)

// Dark Backgrounds & Surfaces
val DarkBackground = Color(0xFF11121A)
val DarkSurface = Color(0xFF1A1C28)
val DarkSurfaceSubtle = Color(0xFF232637)
val DarkBorder = Color(0xFF2D3045)
val DarkTextPrimary = Color(0xFFF4F6FC)
val DarkTextSecondary = Color(0xFFA0A6BC)
val DarkTextTertiary = Color(0xFF6B7288)

// OLED Pitch Black Backgrounds & Surfaces
val OledBackground = Color(0xFF000000)
val OledSurface = Color(0xFF0E0E12)
val OledSurfaceSubtle = Color(0xFF16161C)
val OledBorder = Color(0xFF22222C)
val OledTextPrimary = Color(0xFFFFFFFF)
val OledTextSecondary = Color(0xFFA6AAB8)
val OledTextTertiary = Color(0xFF606473)

// Status colors
val PriorityHigh = Color(0xFFFF4D4D)
val PriorityHighBg = Color(0xFFFFECEC)
val PriorityMedium = Color(0xFFF59E0B)
val PriorityMediumBg = Color(0xFFFEF9E8)
val PriorityLow = Color(0xFF3B82F6)
val PriorityLowBg = Color(0xFFEBF3FE)

data class AccentPalette(
    val name: String,
    val displayName: String,
    val primary: Color,
    val primaryLight: Color,
    val primaryDark: Color
)

val AccentPalettes = mapOf(
    "Lavender" to AccentPalette("Lavender", "Lavender Bliss", PastelLavender, PastelLavenderLight, PastelLavenderDark),
    "Matcha" to AccentPalette("Matcha", "Matcha Sage", PastelMatcha, PastelMatchaLight, PastelMatchaDark),
    "Sakura" to AccentPalette("Sakura", "Sakura Rose", PastelSakura, PastelSakuraLight, PastelSakuraDark),
    "Peach" to AccentPalette("Peach", "Peach Sunset", PastelPeach, PastelPeachLight, PastelPeachDark),
    "Ocean" to AccentPalette("Ocean", "Ocean Breeze", PastelOcean, PastelOceanLight, PastelOceanDark),
    "Mocha" to AccentPalette("Mocha", "Mocha Latte", PastelMocha, PastelMochaLight, PastelMochaDark),
    "Twilight" to AccentPalette("Twilight", "Midnight Twilight", PastelTwilight, PastelTwilightLight, PastelTwilightDark),
    "Mint" to AccentPalette("Mint", "Fresh Mint", PastelMint, PastelMintLight, PastelMintDark),
    "Berry" to AccentPalette("Berry", "Berry Crimson", PastelBerry, PastelBerryLight, PastelBerryDark),
    "Forest" to AccentPalette("Forest", "Nordic Forest", PastelForest, PastelForestLight, PastelForestDark)
)

