package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.data.settings.AppFontFamily
import com.example.data.settings.AppFontSize
import com.example.data.settings.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF93C5FD),
    onPrimary = Color(0xFF1E3A8A),
    primaryContainer = Color(0xFF1E40AF),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = Color(0xFFA5B4FC),
    onSecondary = Color(0xFF312E81),
    secondaryContainer = Color(0xFF3730A3),
    onSecondaryContainer = Color(0xFFE0E7FF),
    tertiary = Color(0xFFFDE68A),
    onTertiary = Color(0xFF78350F),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF64748B)
)

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = IndigoOnPrimary,
    primaryContainer = IndigoPrimaryContainer,
    onPrimaryContainer = IndigoOnPrimaryContainer,
    secondary = SlateSecondary,
    onSecondary = SlateOnSecondary,
    secondaryContainer = SlateSecondaryContainer,
    onSecondaryContainer = SlateOnSecondaryContainer,
    tertiary = AmberTertiary,
    onTertiary = AmberOnTertiary,
    tertiaryContainer = AmberTertiaryContainer,
    onTertiaryContainer = AmberOnTertiaryContainer,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1)
)

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    fontFamilyChoice: AppFontFamily = AppFontFamily.DEFAULT,
    fontSizeChoice: AppFontSize = AppFontSize.NORMAL,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    val composeFontFamily = when (fontFamilyChoice) {
        AppFontFamily.SERIF -> FontFamily.Serif
        AppFontFamily.MONOSPACE -> FontFamily.Monospace
        AppFontFamily.DEFAULT -> FontFamily.Default
    }

    val scale = fontSizeChoice.scaleFactor

    val appTypography = remember(composeFontFamily, scale) {
        Typography(
            displayLarge = TextStyle(
                fontFamily = composeFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = (57 * scale).sp,
                lineHeight = (64 * scale).sp
            ),
            displayMedium = TextStyle(
                fontFamily = composeFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = (45 * scale).sp,
                lineHeight = (52 * scale).sp
            ),
            displaySmall = TextStyle(
                fontFamily = composeFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = (36 * scale).sp,
                lineHeight = (44 * scale).sp
            ),
            headlineLarge = TextStyle(
                fontFamily = composeFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = (32 * scale).sp,
                lineHeight = (40 * scale).sp
            ),
            headlineMedium = TextStyle(
                fontFamily = composeFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = (28 * scale).sp,
                lineHeight = (36 * scale).sp
            ),
            headlineSmall = TextStyle(
                fontFamily = composeFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = (24 * scale).sp,
                lineHeight = (32 * scale).sp
            ),
            titleLarge = TextStyle(
                fontFamily = composeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (22 * scale).sp,
                lineHeight = (28 * scale).sp
            ),
            titleMedium = TextStyle(
                fontFamily = composeFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = (16 * scale).sp,
                lineHeight = (24 * scale).sp
            ),
            titleSmall = TextStyle(
                fontFamily = composeFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = (14 * scale).sp,
                lineHeight = (20 * scale).sp
            ),
            bodyLarge = TextStyle(
                fontFamily = composeFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = (16 * scale).sp,
                lineHeight = (24 * scale).sp,
                letterSpacing = 0.5.sp
            ),
            bodyMedium = TextStyle(
                fontFamily = composeFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = (14 * scale).sp,
                lineHeight = (20 * scale).sp,
                letterSpacing = 0.25.sp
            ),
            bodySmall = TextStyle(
                fontFamily = composeFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = (12 * scale).sp,
                lineHeight = (16 * scale).sp,
                letterSpacing = 0.4.sp
            ),
            labelLarge = TextStyle(
                fontFamily = composeFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = (14 * scale).sp,
                lineHeight = (20 * scale).sp,
                letterSpacing = 0.1.sp
            ),
            labelMedium = TextStyle(
                fontFamily = composeFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = (12 * scale).sp,
                lineHeight = (16 * scale).sp,
                letterSpacing = 0.5.sp
            ),
            labelSmall = TextStyle(
                fontFamily = composeFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = (11 * scale).sp,
                lineHeight = (16 * scale).sp,
                letterSpacing = 0.5.sp
            )
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = appTypography,
        content = content
    )
}
