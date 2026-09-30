package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.ContentGenerator
import com.example.data.auth.AuthManager
import com.example.data.local.AppDatabase
import com.example.data.local.CreationEntity
import com.example.data.model.AppThemeMode
import com.example.data.model.ContentType
import com.example.data.model.CreationItem
import com.example.data.model.TrendingCategory
import com.example.data.model.TrendingHashtag
import com.example.data.model.TrendingHook
import com.example.data.model.TrendingReelIdea
import com.example.data.model.UserProfile
import com.example.data.speech.VoiceInputManager
import com.example.data.speech.VoiceState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class GenerationState {
    object Idle : GenerationState()
    data class Generating(val step: String, val progress: Float) : GenerationState()
    data class Success(val item: CreationItem) : GenerationState()
    data class Error(val message: String) : GenerationState()
}

class VibeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val dao = db.creationDao()
    private val contentGenerator = ContentGenerator()
    val authManager = AuthManager(application)
    val voiceInputManager = VoiceInputManager(application)

    // Current Prompt & Settings
    val promptInput = MutableStateFlow("")
    val selectedContentType = MutableStateFlow(ContentType.VIRAL_CAPTION)
    val selectedTone = MutableStateFlow("Hype")
    val selectedPlatform = MutableStateFlow("Instagram & TikTok")

    // Generation State
    private val _generationState = MutableStateFlow<GenerationState>(GenerationState.Idle)
    val generationState: StateFlow<GenerationState> = _generationState.asStateFlow()

    // Currently active creation item in Creator Screen
    private val _currentCreation = MutableStateFlow<CreationItem?>(null)
    val currentCreation: StateFlow<CreationItem?> = _currentCreation.asStateFlow()

    // Photo Studio State
    val selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedPhotoPreset = MutableStateFlow("AI Caption")

    // Reel Script State
    val reelTopic = MutableStateFlow("")
    val reelDuration = MutableStateFlow("30s")

    // History and Search
    val searchQuery = MutableStateFlow("")
    val selectedCategoryFilter = MutableStateFlow<ContentType?>(null)

    val allCreations: StateFlow<List<CreationItem>> = dao.getAllCreations()
        .combine(searchQuery) { entities, query ->
            val list = entities.map { it.toCreationItem() }
            if (query.isBlank()) list else list.filter {
                it.prompt.contains(query, ignoreCase = true) ||
                it.title.contains(query, ignoreCase = true) ||
                it.generatedContent.contains(query, ignoreCase = true)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteCreations: StateFlow<List<CreationItem>> = dao.getFavoriteCreations()
        .combine(searchQuery) { entities, query ->
            val list = entities.map { it.toCreationItem() }
            if (query.isBlank()) list else list.filter {
                it.prompt.contains(query, ignoreCase = true) ||
                it.generatedContent.contains(query, ignoreCase = true)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Profile & Settings
    private val prefs = application.getSharedPreferences("vibe_prefs", Context.MODE_PRIVATE)
    private val _userProfile = MutableStateFlow(
        UserProfile(
            username = prefs.getString("username", "VibeCreator") ?: "VibeCreator",
            handle = prefs.getString("handle", "@vibe_creator") ?: "@vibe_creator",
            bio = prefs.getString("bio", "Digital Creator & Storyteller 🚀 Turning everyday vibes into viral moments.") ?: "",
            themeMode = try {
                AppThemeMode.valueOf(prefs.getString("theme_mode", AppThemeMode.DARK.name) ?: AppThemeMode.DARK.name)
            } catch (_: Exception) { AppThemeMode.DARK },
            customGeminiApiKey = prefs.getString("gemini_key", "") ?: ""
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Notification / Toast Events
    private val _uiEvents = MutableSharedFlow<String>()
    val uiEvents: SharedFlow<String> = _uiEvents.asSharedFlow()

    // Trending Data
    val trendingCategories = listOf(
        TrendingCategory("1", "Tech & AI", "🤖", "2.8M posts"),
        TrendingCategory("2", "Aesthetic & Film", "🎬", "4.1M posts"),
        TrendingCategory("3", "Hustle & Career", "💼", "1.9M posts"),
        TrendingCategory("4", "Fitness & Glow Up", "⚡", "3.3M posts"),
        TrendingCategory("5", "Relatable Comedy", "😂", "5.6M posts"),
        TrendingCategory("6", "Travel & Lifestyle", "✈️", "2.2M posts")
    )

    val trendingHooks = listOf(
        TrendingHook("h1", "POV: You finally stopped doing this in 2026...", "Relatable", "98% Virality", "Creates instant FOMO and relatability"),
        TrendingHook("h2", "The 1 secret that changed my entire workflow in 7 days", "Hustle", "96% Virality", "High curiosity gap and time-bound claim"),
        TrendingHook("h3", "Stop scrolling if you want to master this before everyone else", "Tech", "94% Virality", "Command hook with forward urgency"),
        TrendingHook("h4", "3 things nobody tells you about building an audience from 0", "Growth", "97% Virality", "Numbered value list with insider framing"),
        TrendingHook("h5", "Unpopular opinion: this is why most creators fail within 30 days", "Opinion", "95% Virality", "Contrarian hook driving comment debates")
    )

    val trendingHashtags = listOf(
        TrendingHashtag("#AITools2026", "1.4M", "+420%", "Tech"),
        TrendingHashtag("#GrowthMindset", "8.9M", "+180%", "Hustle"),
        TrendingHashtag("#ReelsViral", "24.5M", "+310%", "General"),
        TrendingHashtag("#DayInMyLife", "16.2M", "+145%", "Lifestyle"),
        TrendingHashtag("#AlgorithmHacks", "3.7M", "+520%", "Social"),
        TrendingHashtag("#AestheticVibe", "11.1M", "+210%", "Visual")
    )

    val trendingReelIdeas = listOf(
        TrendingReelIdea(
            id = "r1",
            title = "The 3-Second Rule Breakdown",
            format = "Talking Head + Fast B-Roll",
            audioTrend = "Cyberwave Ambient (Trending +80k)",
            hookSuggestion = "If your reels get stuck at 200 views, watch this first.",
            whyItWorks = "Solves the #1 creator struggle with actionable visual pacing.",
            viralScore = 97
        ),
        TrendingReelIdea(
            id = "r2",
            title = "Aesthetic Day in 2026 (Zero Voiceover)",
            format = "Visual ASMR + Text Overlays",
            audioTrend = "Lo-Fi Midnight Drift (Trending +120k)",
            hookSuggestion = "POV: Finding calm in the middle of modern chaos.",
            whyItWorks = "High replay rate due to pleasing visual rhythm and text pacing.",
            viralScore = 95
        ),
        TrendingReelIdea(
            id = "r3",
            title = "Before vs After Workflow Transformation",
            format = "Split Screen Comparison",
            audioTrend = "Upbeat Electro Drop (Trending +95k)",
            hookSuggestion = "How I used to work vs how I do it today.",
            whyItWorks = "Instant visual contrast creates immediate engagement.",
            viralScore = 98
        )
    )

    init {
        // Pre-populate with sample creations if database is empty so users see rich history immediately
        viewModelScope.launch {
            dao.getAllCreations().collect { list ->
                if (list.isEmpty()) {
                    seedInitialCreations()
                }
            }
        }
    }

    private suspend fun seedInitialCreations() {
        val sample1 = CreationEntity(
            title = "AI Productivity Workflow",
            prompt = "How to automate social media content with AI in 2026",
            contentType = ContentType.VIRAL_CAPTION.name,
            generatedContent = "Most people waste 4 hours creating content. Here is the modern 2026 playbook to do it in 15 minutes.\n\n1. Capture raw thoughts on the go\n2. Let AI structure the hook and pacing\n3. Post with intentional engagement questions\n\nSave this for your next batching session! 🚀",
            hooksJson = "POV: You stopped spending 4 hours on 1 post|||Stop scrolling if you want to automate your week|||The exact 2026 AI toolstack nobody shares",
            hashtagsJson = "#ProductivityHacks|||#AITools|||#ContentBatching|||#CreatorEconomy|||#ViralGrowth",
            cta = "Save this & comment 'PROMPT' for the full template!",
            script15s = "",
            script30s = "",
            platform = "Instagram & TikTok",
            tone = "Hype",
            viralScore = 96,
            timestamp = System.currentTimeMillis() - 86400000L,
            isFavorite = true,
            imageUri = null
        )
        val sample2 = CreationEntity(
            title = "Reel: Morning Mindset Shift",
            prompt = "5-minute routine that rewires your brain for focus",
            contentType = ContentType.REEL_SCRIPT.name,
            generatedContent = "The 5-minute morning protocol that changed everything 🧠\n\nNo phone for the first 30 minutes, 16oz hydration, and 3 high-leverage priorities written down.",
            hooksJson = "Don't touch your phone when you wake up—do this instead|||The 5-minute routine millionaires swear by",
            hashtagsJson = "#MorningRoutine|||#MindsetShift|||#FocusTips|||#ReelsGrowth",
            cta = "Double tap if you're trying this tomorrow morning!",
            script15s = "[0-3s] Hold cold water glass. 'Stop checking your phone first thing.'\n[3-10s] Quick clips: journal, morning sunlight.\n[10-15s] 'Try this for 3 days and thank me later.'",
            script30s = "[0-3s] Dramatic stare at alarm clock.\n[3-15s] Explain the dopamine crash of early screen time.\n[15-25s] Give the 3 replacement habits.\n[25-30s] Call to action to save.",
            platform = "Reels & Shorts",
            tone = "Inspiring",
            viralScore = 94,
            timestamp = System.currentTimeMillis() - 172800000L,
            isFavorite = false,
            imageUri = null
        )
        dao.insertCreation(sample1)
        dao.insertCreation(sample2)
    }

    fun generate(
        prompt: String = promptInput.value,
        contentType: ContentType = selectedContentType.value,
        tone: String = selectedTone.value,
        platform: String = selectedPlatform.value,
        imageUri: Uri? = selectedImageUri.value,
        photoPreset: String? = if (contentType == ContentType.PHOTO_STUDIO) selectedPhotoPreset.value else null,
        onNavigateToCreator: () -> Unit = {}
    ) {
        if (prompt.isBlank() && contentType != ContentType.PHOTO_STUDIO) {
            viewModelScope.launch {
                _uiEvents.emit("Please enter an idea or prompt first ✨")
            }
            return
        }

        viewModelScope.launch {
            _generationState.value = GenerationState.Generating("Analyzing viral patterns...", 0.2f)
            onNavigateToCreator()

            _generationState.value = GenerationState.Generating("Hooking your target audience...", 0.5f)
            _generationState.value = GenerationState.Generating("Polishing copy & algorithms...", 0.8f)

            try {
                val item = contentGenerator.generateContent(
                    prompt = prompt.ifBlank { "Viral visual vibe" },
                    contentType = contentType,
                    tone = tone,
                    platform = platform,
                    imageUri = imageUri?.toString(),
                    photoPreset = photoPreset,
                    userGeminiKey = _userProfile.value.customGeminiApiKey
                )

                _currentCreation.value = item
                _generationState.value = GenerationState.Success(item)

                // Auto-save to local database
                val entity = CreationEntity.fromCreationItem(item)
                val newId = dao.insertCreation(entity)
                _currentCreation.value = item.copy(id = newId)

                // Increment profile creations count
                val newCount = _userProfile.value.totalCreations + 1
                _userProfile.value = _userProfile.value.copy(totalCreations = newCount)
                prefs.edit().putInt("total_creations", newCount).apply()

            } catch (e: Exception) {
                _generationState.value = GenerationState.Error(e.message ?: "Failed to generate content")
            }
        }
    }

    fun regenerate() {
        val current = _currentCreation.value ?: return
        generate(
            prompt = current.prompt,
            contentType = current.contentType,
            tone = current.tone,
            platform = current.platform,
            imageUri = current.imageUri?.let { Uri.parse(it) }
        )
    }

    fun updateGeneratedContent(newContent: String) {
        val current = _currentCreation.value ?: return
        val updated = current.copy(generatedContent = newContent)
        _currentCreation.value = updated
        viewModelScope.launch {
            dao.updateCreation(CreationEntity.fromCreationItem(updated))
            _uiEvents.emit("Changes saved ✨")
        }
    }

    fun toggleFavorite(item: CreationItem) {
        viewModelScope.launch {
            val updated = !item.isFavorite
            dao.setFavorite(item.id, updated)
            if (_currentCreation.value?.id == item.id) {
                _currentCreation.value = _currentCreation.value?.copy(isFavorite = updated)
            }
            _uiEvents.emit(if (updated) "Saved to Favorites ❤️" else "Removed from Favorites")
        }
    }

    fun deleteCreation(id: Long) {
        viewModelScope.launch {
            dao.deleteCreationById(id)
            if (_currentCreation.value?.id == id) {
                _currentCreation.value = null
            }
            _uiEvents.emit("Item deleted 🗑️")
        }
    }

    fun copyToClipboard(context: Context, text: String, label: String = "VibeAI Content") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
    }

    fun shareContent(context: Context, text: String, title: String = "Share via VibeAI") {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        context.startActivity(shareIntent)
    }

    fun useTrendingIdea(hookOrTopic: String, contentType: ContentType = ContentType.VIRAL_CAPTION, onNavigateToHome: () -> Unit) {
        promptInput.value = hookOrTopic
        selectedContentType.value = contentType
        onNavigateToHome()
    }

    fun onPhotoSelected(uri: Uri?) {
        selectedImageUri.value = uri
        if (uri != null && selectedContentType.value != ContentType.PHOTO_STUDIO) {
            selectedContentType.value = ContentType.PHOTO_STUDIO
        }
    }

    fun updateTheme(themeMode: AppThemeMode) {
        _userProfile.value = _userProfile.value.copy(themeMode = themeMode)
        prefs.edit().putString("theme_mode", themeMode.name).apply()
    }

    fun updateCustomApiKey(key: String) {
        _userProfile.value = _userProfile.value.copy(customGeminiApiKey = key.trim())
        prefs.edit().putString("gemini_key", key.trim()).apply()
        viewModelScope.launch {
            _uiEvents.emit(if (key.isNotBlank()) "Gemini API key saved! 🔑" else "Using default AI engine")
        }
    }

    fun updateProfile(name: String, bio: String) {
        _userProfile.value = _userProfile.value.copy(
            username = name.ifBlank { "VibeCreator" },
            handle = "@${name.lowercase().replace(" ", "_")}",
            bio = bio
        )
        prefs.edit()
            .putString("username", name)
            .putString("bio", bio)
            .apply()
        viewModelScope.launch {
            _uiEvents.emit("Profile updated ✨")
        }
    }
}
