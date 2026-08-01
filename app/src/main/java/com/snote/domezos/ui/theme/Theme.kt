package com.snote.domezos.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalAppThemeAccent = staticCompositionLocalOf { Color.Unspecified }

@Composable
fun DwSecretNotesTheme(
    themeConfig: AppThemeConfig = ClassicTheme,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val isLightBackground = themeConfig.colorScheme.background.luminance() > 0.5f
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = isLightBackground
            insetsController.isAppearanceLightNavigationBars = isLightBackground
        }
    }
    CompositionLocalProvider(LocalAppThemeAccent provides themeConfig.accentColor) {
        MaterialTheme(
            colorScheme = themeConfig.colorScheme,
            typography  = DwTypography,
            content     = content
        )
    }
}
