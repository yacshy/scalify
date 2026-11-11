package com.example.scalify.ui.theme

import android.app.Activity
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
import com.example.hair.ui.theme.AccentPurple
import com.example.hair.ui.theme.Danger
import com.example.hair.ui.theme.Gold
import com.example.hair.ui.theme.GoldSoft
import com.example.hair.ui.theme.Ink
import com.example.hair.ui.theme.InkSoft
import com.example.hair.ui.theme.Line
import com.example.hair.ui.theme.LineStrong
import com.example.hair.ui.theme.Paper
import com.example.hair.ui.theme.Sage
import com.example.hair.ui.theme.SageDeep
import com.example.hair.ui.theme.Surface2
import com.example.hair.ui.theme.SurfaceWhite
import com.example.hair.ui.theme.Typography

private val LightColorScheme = lightColorScheme(
    primary = Sage,
    onPrimary = Color.White,
    primaryContainer = SageDeep,
    onPrimaryContainer = Color.White,
    secondary = Gold,
    onSecondary = Color.White,
    secondaryContainer = GoldSoft,
    onSecondaryContainer = Ink,
    tertiary = AccentPurple,
    onTertiary = Color.White,
    background = Paper,
    onBackground = Ink,
    surface = SurfaceWhite,
    onSurface = Ink,
    surfaceVariant = Surface2,
    onSurfaceVariant = InkSoft,
    outline = LineStrong,
    outlineVariant = Line,
    error = Danger,
    onError = Color.White
)


@Composable
fun ScalifyTheme(
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

        darkTheme -> LightColorScheme // DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}