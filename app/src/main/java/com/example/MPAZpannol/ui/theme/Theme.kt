
package com.example.MPAZpannol.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colores para el modo claro
private val LightColorScheme = lightColorScheme(
    primary = AzulMPAZ,
    onPrimary = Color.White,

    primaryContainer = AzulClaroMPAZ,
    onPrimaryContainer = TextoMPAZ,

    secondary = NaranjaMPAZ,
    onSecondary = Color.White,

    background = FondoMPAZ,
    onBackground = TextoMPAZ,

    surface = Color.White,
    onSurface = TextoMPAZ,

    outline = AzulMPAZ
)

// Colores para el modo oscuro
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFADC6FF),
    onPrimary = Color(0xFF002D6D),

    secondary = Color(0xFFFFB77A),
    onSecondary = Color(0xFF4E2600),

    background = Color(0xFF101827),
    onBackground = Color(0xFFE9EEFF),

    surface = Color(0xFF182338),
    onSurface = Color(0xFFE9EEFF)
)

@Composable
fun MPAZPañolTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
        content = content
    )
}
