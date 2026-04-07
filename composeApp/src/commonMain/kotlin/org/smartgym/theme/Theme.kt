package org.smartgym.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val SmartGymGreen = Color(0xFFD4FF00)
val SmartGymGreenWhite = Color(0xFFAACC00)
val BackgroundBlack = Color(0xFF0F0F0F)
val CardGray = Color(0xFF1A1A1A)
val TextGray = Color(0xFF8E8E8E)

val LightBackgroundWhite = Color(0xFFEDEDED)
val LightTextGray = Color(0xFF6B6B6B)
val LightCardWhite = Color(0xFFf5f5f5)


val LightColors = lightColorScheme(
    primary = SmartGymGreen,
    onPrimary = Color.Black,
    background = LightBackgroundWhite,
    onBackground = Color(0xFF0F0F0F),
    surface = Color(0xFFf3f3f3),
    onSurface = Color(0xFF0F0F0F),
    onSurfaceVariant = LightTextGray,
    surfaceVariant = Color(0xFFEAEAEA),
    onSecondary = Color.Black,
)

val DarkColors = darkColorScheme(
    primary = SmartGymGreen,
    onPrimary = Color.Black,
    background = BackgroundBlack,
    onBackground = Color.White,
    surface = CardGray,
    onSurface = Color.White,
    onSurfaceVariant = TextGray,
    surfaceVariant = Color(0xFF252525),
    onSecondary = Color.White,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
){
    val colors = if(darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}