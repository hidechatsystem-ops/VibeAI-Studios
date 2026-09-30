package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Neon AI & Futuristic 2026 Palette
val VibeVioletPrimary = Color(0xFF8B5CF6)
val VibeVioletDark = Color(0xFF6D28D9)
val VibeCyan = Color(0xFF06B6D4)
val VibeCyanLight = Color(0xFF22D3EE)
val VibeMagenta = Color(0xFFEC4899)
val VibePink = Color(0xFFF43F5E)
val VibeAmber = Color(0xFFF59E0B)
val VibeEmerald = Color(0xFF10B981)

// Dark Theme Surfaces
val DarkBg = Color(0xFF090B10)
val DarkSurface = Color(0xFF111420)
val DarkCard = Color(0xFF171B2B)
val DarkCardElevated = Color(0xFF20263D)
val DarkBorder = Color(0x33A78BFA)
val DarkBorderHighlight = Color(0x6606B6D4)

// Light Theme Surfaces
val LightBg = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightCard = Color(0xFFF1F5F9)
val LightCardElevated = Color(0xFFFFFFFF)
val LightBorder = Color(0x338B5CF6)

// Text Colors
val TextPrimaryDark = Color(0xFFF8FAFC)
val TextSecondaryDark = Color(0xFF94A3B8)
val TextTertiaryDark = Color(0xFF64748B)

val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF475569)
val TextTertiaryLight = Color(0xFF94A3B8)

// Gradients
val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF8B5CF6), Color(0xFFEC4899), Color(0xFF06B6D4))
)

val GlowGradient = Brush.radialGradient(
    colors = listOf(Color(0x338B5CF6), Color(0x008B5CF6))
)

val CardGradientDark = Brush.verticalGradient(
    colors = listOf(Color(0xFF1C2238), Color(0xFF121626))
)

val CardGradientLight = Brush.verticalGradient(
    colors = listOf(Color(0xFFFFFFFF), Color(0xFFF8FAFC))
)
