package com.example.data.model

enum class ContentType(val displayName: String, val icon: String) {
    VIRAL_CAPTION("Viral Caption", "🔥"),
    AI_PHOTO_IDEA("AI Photo Idea", "📸"),
    REEL_SCRIPT("Reel Script", "🎬"),
    HASHTAGS("Hashtags", "🏷️"),
    STORY_GENERATOR("Story Generator", "✨"),
    BIO_GENERATOR("Bio Generator", "💫"),
    PHOTO_STUDIO("Photo Studio", "🎨")
}

data class CreationItem(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val prompt: String,
    val contentType: ContentType,
    val generatedContent: String,
    val hooks: List<String> = emptyList(),
    val hashtags: List<String> = emptyList(),
    val cta: String = "",
    val script15s: String = "",
    val script30s: String = "",
    val platform: String = "Instagram & TikTok",
    val tone: String = "Hype & Viral",
    val viralScore: Int = 92,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val imageUri: String? = null
)
