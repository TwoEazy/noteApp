package com.noteapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Black and Gold Color Palette
object BlackGoldColors {
    val Gold = Color(0xFFFFD700)
    val DarkGold = Color(0xFFB8860B)
    val LightGold = Color(0xFFFFF8DC)
    val DeepBlack = Color(0xFF000000)
    val CharcoalBlack = Color(0xFF1C1C1C)
    val DarkGray = Color(0xFF2D2D2D)
    val MediumGray = Color(0xFF404040)
    val LightGray = Color(0xFF666666)
    val White = Color(0xFFFFFFFF)
    val ErrorRed = Color(0xFFFF6B6B)
    val SuccessGreen = Color(0xFF51CF66)
}

private val DarkColorScheme = darkColorScheme(
    primary = BlackGoldColors.Gold,
    onPrimary = BlackGoldColors.DeepBlack,
    primaryContainer = BlackGoldColors.DarkGold,
    onPrimaryContainer = BlackGoldColors.LightGold,

    secondary = BlackGoldColors.LightGold,
    onSecondary = BlackGoldColors.DeepBlack,
    secondaryContainer = BlackGoldColors.DarkGray,
    onSecondaryContainer = BlackGoldColors.LightGold,

    tertiary = BlackGoldColors.DarkGold,
    onTertiary = BlackGoldColors.DeepBlack,

    background = BlackGoldColors.DeepBlack,
    onBackground = BlackGoldColors.LightGold,

    surface = BlackGoldColors.CharcoalBlack,
    onSurface = BlackGoldColors.LightGold,
    surfaceVariant = BlackGoldColors.DarkGray,
    onSurfaceVariant = BlackGoldColors.Gold,

    outline = BlackGoldColors.MediumGray,
    outlineVariant = BlackGoldColors.LightGray,

    error = BlackGoldColors.ErrorRed,
    onError = BlackGoldColors.White,
    errorContainer = Color(0xFF4A1616),
    onErrorContainer = BlackGoldColors.ErrorRed,

    inverseSurface = BlackGoldColors.LightGold,
    inverseOnSurface = BlackGoldColors.DeepBlack,
    inversePrimary = BlackGoldColors.DarkGold,

    surfaceTint = BlackGoldColors.Gold,
    scrim = BlackGoldColors.DeepBlack.copy(alpha = 0.8f)
)

private val LightColorScheme = lightColorScheme(
    primary = BlackGoldColors.DarkGold,
    onPrimary = BlackGoldColors.White,
    primaryContainer = BlackGoldColors.LightGold,
    onPrimaryContainer = BlackGoldColors.DeepBlack,

    secondary = BlackGoldColors.DeepBlack,
    onSecondary = BlackGoldColors.Gold,
    secondaryContainer = BlackGoldColors.LightGold,
    onSecondaryContainer = BlackGoldColors.DeepBlack,

    tertiary = BlackGoldColors.Gold,
    onTertiary = BlackGoldColors.DeepBlack,

    background = BlackGoldColors.LightGold,
    onBackground = BlackGoldColors.DeepBlack,

    surface = BlackGoldColors.White,
    onSurface = BlackGoldColors.DeepBlack,
    surfaceVariant = BlackGoldColors.LightGold,
    onSurfaceVariant = BlackGoldColors.DarkGold,

    outline = BlackGoldColors.MediumGray,
    outlineVariant = BlackGoldColors.LightGray,

    error = BlackGoldColors.ErrorRed,
    onError = BlackGoldColors.White,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = BlackGoldColors.ErrorRed,

    inverseSurface = BlackGoldColors.CharcoalBlack,
    inverseOnSurface = BlackGoldColors.LightGold,
    inversePrimary = BlackGoldColors.Gold,

    surfaceTint = BlackGoldColors.DarkGold,
    scrim = BlackGoldColors.DeepBlack.copy(alpha = 0.5f)
)

@Composable
fun NoteTheme(
    darkTheme: Boolean = true, // Default to dark theme for black & gold
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}