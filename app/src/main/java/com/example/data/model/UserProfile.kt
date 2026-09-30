package com.example.data.model

enum class AppThemeMode {
    SYSTEM, DARK, LIGHT
}

data class UserProfile(
    val id: String = "guest_001",
    val username: String = "VibeCreator",
    val handle: String = "@vibe_creator",
    val bio: String = "Digital Creator & Storyteller 🚀 Turning everyday vibes into viral moments.",
    val isGuest: Boolean = true,
    val email: String? = null,
    val avatarUrl: String? = null,
    val totalCreations: Int = 18,
    val savedCreationsCount: Int = 5,
    val avgViralScore: Int = 94,
    val themeMode: AppThemeMode = AppThemeMode.DARK,
    val defaultTone: String = "Hype",
    val defaultPlatform: String = "TikTok & Reels",
    val customGeminiApiKey: String = ""
)
