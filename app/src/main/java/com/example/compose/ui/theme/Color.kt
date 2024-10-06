package com.example.compose.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Màu sắc cho chế độ sáng
val LightColorScheme = lightColorScheme(
    primary = Color(0xFFCAAFCF),
    onPrimary = Color.White,
    secondary = Color(0xFF881946),
    onSecondary = Color.Black,
    background = Color.White,
    surface = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF262A3A),
    onPrimary = Color.Black,
    secondary = Color(0xFF03DAC5),
    onSecondary = Color.White,
    background = Color(0xFF0F0D11),
    surface = Color(0xFF1C2350),
    onBackground = Color.White,
    onSurface = Color.White,
)
