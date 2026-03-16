package com.cariboa.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = Cream,
    secondary = WarmBrown,
    onSecondary = Cream,
    surface = Cream,
    onSurface = DarkBrown,
    background = LightTan,
    onBackground = DarkBrown,
    error = MutedRed,
)

private val DarkColorScheme = darkColorScheme(
    primary = ForestGreenLight,
    onPrimary = DarkBrown,
    secondary = WarmBrownLight,
    onSecondary = DarkBrown,
    surface = DarkSurface,
    onSurface = Cream,
    background = DarkBackground,
    onBackground = Cream,
    error = MutedRed,
)

@Composable
fun CaribouTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = CaribouTypography,
        shapes = CaribouShapes,
        content = content,
    )
}
