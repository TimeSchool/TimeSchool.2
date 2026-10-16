package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TimeSchoolDarkColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = BackgroundDark,
    primaryContainer = NeonGreenDim,
    onPrimaryContainer = TextWhite,
    
    secondary = ElectricCyan,
    onSecondary = BackgroundDark,
    secondaryContainer = SlateGray,
    onSecondaryContainer = TextWhite,
    
    tertiary = AmberWarning,
    onTertiary = BackgroundDark,
    
    background = BackgroundDark,
    onBackground = TextWhite,
    
    surface = SurfaceDark,
    onSurface = TextWhite,
    surfaceVariant = CardSurfaceDark,
    onSurfaceVariant = TextSecondary,
    
    error = CrimsonNeon,
    onError = TextWhite,
    errorContainer = CrimsonNeonDim,
    onErrorContainer = TextWhite,
    
    outline = CardBorderDark,
    outlineVariant = SlateGray
)

@Composable
fun TimeSchoolTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = TimeSchoolDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = BackgroundDark.toArgb()
                window.navigationBarColor = BackgroundDark.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    TimeSchoolTheme(darkTheme = darkTheme, content = content)
}
