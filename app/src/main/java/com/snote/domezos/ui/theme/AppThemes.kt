package com.snote.domezos.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance

data class AppThemeConfig(
    val id: String,
    val nameRes: Int,
    val colorScheme: ColorScheme,
    val accentColor: Color
)

private fun contrastRatio(a: Color, b: Color): Double {
    val l1 = a.luminance().toDouble()
    val l2 = b.luminance().toDouble()
    val hi = maxOf(l1, l2)
    val lo = minOf(l1, l2)
    return (hi + 0.05) / (lo + 0.05)
}

/** Picks whichever of black/white contrasts better against [base]. */
private fun bestOnColor(base: Color): Color =
    if (contrastRatio(Color.Black, base) >= contrastRatio(Color.White, base)) Color.Black else Color.White

/** Nudges [base] towards [target] until it reaches [minRatio] contrast against [background]. */
private fun ensureReadableToward(base: Color, background: Color, minRatio: Double, target: Color): Color {
    if (contrastRatio(base, background) >= minRatio) return base
    var lo = 0f
    var hi = 1f
    var result = target
    repeat(14) {
        val mid = (lo + hi) / 2f
        val candidate = lerp(base, target, mid)
        if (contrastRatio(candidate, background) >= minRatio) {
            result = candidate
            hi = mid
        } else {
            lo = mid
        }
    }
    return result
}

/** Nudges [base] towards white until it reaches [minRatio] contrast against [background]. */
private fun ensureReadable(base: Color, background: Color, minRatio: Double = 4.5): Color =
    ensureReadableToward(base, background, minRatio, Color.White)

/** Nudges [base] towards black until it reaches [minRatio] contrast against [background]. */
private fun ensureReadableDark(base: Color, background: Color, minRatio: Double = 4.5): Color =
    ensureReadableToward(base, background, minRatio, Color.Black)

private val NearWhite = Color(0xFFF2F2F5)
private val NearBlack = Color(0xFF1A1A1A)

/** Builds a theme from its own distinct dark background/surface plus two vivid accent colors.
 *  Every theme gets a genuinely different background (so themes are visually distinct from one
 *  another), while text always stays crisp near-white (never tinted pastel) so contrast inside
 *  each theme stays high regardless of which background hue it uses. */
private fun dwColorScheme(
    background: Color,
    surface: Color,
    primary: Color,
    secondary: Color
): ColorScheme {
    val onSurface = ensureReadable(NearWhite, surface, 7.0)
    val surfaceVariant = lerp(surface, onSurface, 0.12f)
    val onSurfaceVariant = lerp(onSurface, background, 0.3f)
    val outline = lerp(onSurface, background, 0.45f)
    return darkColorScheme(
        primary = primary,
        onPrimary = bestOnColor(primary),
        primaryContainer = lerp(primary, background, 0.55f),
        onPrimaryContainer = bestOnColor(lerp(primary, background, 0.55f)),
        secondary = secondary,
        onSecondary = bestOnColor(secondary),
        secondaryContainer = lerp(secondary, background, 0.55f),
        onSecondaryContainer = bestOnColor(lerp(secondary, background, 0.55f)),
        background = background,
        onBackground = onSurface,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        outline = outline,
        tertiary = ensureReadable(secondary, surface, 4.5)
    )
}

/** Light-mode counterpart of [dwColorScheme]: same idea, but text stays crisp near-black on a
 *  bright background/surface, so contrast stays high regardless of which background hue is used. */
private fun dwLightColorScheme(
    background: Color,
    surface: Color,
    primary: Color,
    secondary: Color
): ColorScheme {
    val onSurface = ensureReadableDark(NearBlack, surface, 7.0)
    val surfaceVariant = lerp(surface, onSurface, 0.08f)
    val onSurfaceVariant = lerp(onSurface, background, 0.35f)
    val outline = lerp(onSurface, background, 0.5f)
    return lightColorScheme(
        primary = primary,
        onPrimary = bestOnColor(primary),
        primaryContainer = lerp(primary, background, 0.75f),
        onPrimaryContainer = bestOnColor(lerp(primary, background, 0.75f)),
        secondary = secondary,
        onSecondary = bestOnColor(secondary),
        secondaryContainer = lerp(secondary, background, 0.75f),
        onSecondaryContainer = bestOnColor(lerp(secondary, background, 0.75f)),
        background = background,
        onBackground = onSurface,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        outline = outline,
        tertiary = ensureReadableDark(secondary, surface, 4.5)
    )
}

val ClassicTheme = AppThemeConfig(
    id = "classic",
    nameRes = com.snote.domezos.R.string.theme_classic,
    colorScheme = darkColorScheme(
        primary = CyanAccent,
        onPrimary = Color(0xFF001F2B),
        primaryContainer = Color(0xFF004D66),
        onPrimaryContainer = Color(0xFFB3EFFF),
        secondary = GoldAccent,
        onSecondary = Color(0xFF3F2E00),
        background = Navy900,
        onBackground = OffWhite,
        surface = Navy800,
        onSurface = OffWhite,
        surfaceVariant = Navy700,
        onSurfaceVariant = MutedBlue,
        outline = MutedDark,
        tertiary = Color(0xFF91D8FF)
    ),
    accentColor = GoldAccent
)

val MidnightTheme = AppThemeConfig(
    id = "midnight",
    nameRes = com.snote.domezos.R.string.theme_midnight,
    colorScheme = dwColorScheme(
        background = Color(0xFF0F0817),
        surface = Color(0xFF1B0E29),
        primary = Color(0xFFBB86FC),
        secondary = Color(0xFF03DAC6)
    ),
    accentColor = Color(0xFFBB86FC)
)

val ForestTheme = AppThemeConfig(
    id = "forest",
    nameRes = com.snote.domezos.R.string.theme_forest,
    colorScheme = dwColorScheme(
        background = Color(0xFF081009),
        surface = Color(0xFF0F1F12),
        primary = Color(0xFF81C784),
        secondary = Color(0xFFFFF176)
    ),
    accentColor = Color(0xFF81C784)
)

val OceanTheme = AppThemeConfig(
    id = "ocean",
    nameRes = com.snote.domezos.R.string.theme_ocean,
    colorScheme = dwColorScheme(
        background = Color(0xFF010B13),
        surface = Color(0xFF031A29),
        primary = Color(0xFF4FC3F7),
        secondary = Color(0xFFFFB74D)
    ),
    accentColor = Color(0xFF4FC3F7)
)

val CyberpunkTheme = AppThemeConfig(
    id = "cyberpunk",
    nameRes = com.snote.domezos.R.string.theme_cyberpunk,
    colorScheme = dwColorScheme(
        background = Color(0xFF050505),
        surface = Color(0xFF1A001A),
        primary = Color(0xFFFCEE09),
        secondary = Color(0xFF00E5FF)
    ),
    accentColor = Color(0xFFFCEE09)
)

val DraculaTheme = AppThemeConfig(
    id = "dracula",
    nameRes = com.snote.domezos.R.string.theme_dracula,
    colorScheme = dwColorScheme(
        background = Color(0xFF282A36),
        surface = Color(0xFF343746),
        primary = Color(0xFFBD93F9),
        secondary = Color(0xFFFF79C6)
    ),
    accentColor = Color(0xFFBD93F9)
)

val SunsetTheme = AppThemeConfig(
    id = "sunset",
    nameRes = com.snote.domezos.R.string.theme_sunset,
    colorScheme = dwColorScheme(
        background = Color(0xFF1A0F0E),
        surface = Color(0xFF2E1A17),
        primary = Color(0xFFFF7043),
        secondary = Color(0xFFFFCA28)
    ),
    accentColor = Color(0xFFFF7043)
)

val NordicTheme = AppThemeConfig(
    id = "nordic",
    nameRes = com.snote.domezos.R.string.theme_nordic,
    colorScheme = dwColorScheme(
        background = Color(0xFF232935),
        surface = Color(0xFF3B4252),
        primary = Color(0xFF88C0D0),
        secondary = Color(0xFFA3BE8C)
    ),
    accentColor = Color(0xFF88C0D0)
)

val MatrixTheme = AppThemeConfig(
    id = "matrix",
    nameRes = com.snote.domezos.R.string.theme_matrix,
    colorScheme = dwColorScheme(
        background = Color(0xFF000000),
        surface = Color(0xFF001500),
        primary = Color(0xFF00FF41),
        secondary = Color(0xFF00CC33)
    ),
    accentColor = Color(0xFF00FF41)
)

val SakuraTheme = AppThemeConfig(
    id = "sakura",
    nameRes = com.snote.domezos.R.string.theme_sakura,
    colorScheme = dwColorScheme(
        background = Color(0xFF1D0A14),
        surface = Color(0xFF2D111E),
        primary = Color(0xFFFFB7C5),
        secondary = Color(0xFFD4875A)
    ),
    accentColor = Color(0xFFFFB7C5)
)

val GoldenAgeTheme = AppThemeConfig(
    id = "golden",
    nameRes = com.snote.domezos.R.string.theme_golden,
    colorScheme = dwColorScheme(
        background = Color(0xFF140F07),
        surface = Color(0xFF251C0D),
        primary = Color(0xFFD4AF37),
        secondary = Color(0xFFC8960C)
    ),
    accentColor = Color(0xFFD4AF37)
)

val RubyTheme = AppThemeConfig(
    id = "ruby",
    nameRes = com.snote.domezos.R.string.theme_ruby,
    colorScheme = dwColorScheme(
        background = Color(0xFF1A0407),
        surface = Color(0xFF2E070D),
        primary = Color(0xFFE0115F),
        secondary = Color(0xFFFF8C42)
    ),
    accentColor = Color(0xFFE0115F)
)

val ElectricTheme = AppThemeConfig(
    id = "electric",
    nameRes = com.snote.domezos.R.string.theme_electric,
    colorScheme = dwColorScheme(
        background = Color(0xFF0D001A),
        surface = Color(0xFF1A0033),
        primary = Color(0xFFB44DFF),
        secondary = Color(0xFF00FFFF)
    ),
    accentColor = Color(0xFFB44DFF)
)

val GhostTheme = AppThemeConfig(
    id = "ghost",
    nameRes = com.snote.domezos.R.string.theme_ghost,
    colorScheme = dwColorScheme(
        background = Color(0xFF1C1C1E),
        surface = Color(0xFF2C2C2E),
        primary = Color(0xFF98989D),
        secondary = Color(0xFF636366)
    ),
    accentColor = Color(0xFF98989D)
)

val SolarizedTheme = AppThemeConfig(
    id = "solarized",
    nameRes = com.snote.domezos.R.string.theme_solarized,
    colorScheme = dwColorScheme(
        background = Color(0xFF002B36),
        surface = Color(0xFF073642),
        primary = Color(0xFF268BD2),
        secondary = Color(0xFFB58900)
    ),
    accentColor = Color(0xFF268BD2)
)


/** Plain dark mode: neutral gray background, light (near-white) text. */
val DarkTheme = AppThemeConfig(
    id = "dark",
    nameRes = com.snote.domezos.R.string.theme_dark,
    colorScheme = dwColorScheme(
        background = Color(0xFF121212),
        surface = Color(0xFF1E1E1E),
        primary = CyanAccent,
        secondary = GoldAccent
    ),
    accentColor = CyanAccent
)

/** Plain light mode: neutral near-white background, dark text. */
val LightTheme = AppThemeConfig(
    id = "light",
    nameRes = com.snote.domezos.R.string.theme_light,
    colorScheme = dwLightColorScheme(
        background = Color(0xFFF7F7FA),
        surface = Color(0xFFFFFFFF),
        primary = Color(0xFF0077A6),
        secondary = Color(0xFFB8860B)
    ),
    accentColor = Color(0xFF0077A6)
)

val ALL_THEMES = listOf(
    ClassicTheme, DarkTheme, LightTheme, MidnightTheme, ForestTheme, OceanTheme, CyberpunkTheme,
    DraculaTheme, SunsetTheme, NordicTheme, MatrixTheme, SakuraTheme,
    GoldenAgeTheme, RubyTheme, ElectricTheme, GhostTheme, SolarizedTheme
)
