package com.anidit.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Surfaces
val Ink = Color(0xFF0A0A0F)
val InkSurface = Color(0xFF15151D)
val InkSurfaceAlt = Color(0xFF1D1D28)
val InkLine = Color(0xFF2A2A38)

// Signature accent pair
val Violet = Color(0xFF7C4DFF)
val Magenta = Color(0xFFFF3D5F)
val Cyan = Color(0xFF00E5FF)

// Text
val TextPrimary = Color(0xFFF2F2F7)
val TextSecondary = Color(0xFFA3A3B2)
val TextMuted = Color(0xFF6B6B7A)

val ImpactGradient = Brush.horizontalGradient(listOf(Violet, Magenta))

val AniDitColorScheme = darkColorScheme(
    primary = Violet,
    secondary = Cyan,
    tertiary = Magenta,
    background = Ink,
    surface = InkSurface,
    surfaceVariant = InkSurfaceAlt,
    outline = InkLine,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary
)

val AniDitShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

val AniDitTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.Black, fontSize = 34.sp, lineHeight = 38.sp, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp, color = TextSecondary),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 13.sp, letterSpacing = 1.2.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 1.4.sp, color = TextMuted)
)
