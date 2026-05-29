package com.acurast.wallet.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF9C27B0),      // 紫色
    secondary = Color(0xFF7C4DFF),    // 深紫
    tertiary = Color(0xFFE040FB),     // 亮紫
    background = Color(0xFF1A1A2E),   // 深蓝黑
    surface = Color(0xFF16213E),      // 深蓝
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF7B1FA2),      // 紫色
    secondary = Color(0xFF512DA8),    // 深紫
    tertiary = Color(0xFF9C27B0),     // 紫色
    background = Color(0xFFF5F5F5),   // 浅灰
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF212121),
)

@Composable
fun AcurastWalletTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}