package com.example.data.ai

import com.example.data.model.ContentType
import com.example.data.model.CreationItem
import kotlinx.coroutines.delay
import kotlin.random.Random

class ContentGenerator(private val geminiService: GeminiService = GeminiService()) {

    fun isGeminiConfigured(userKey: String? = null): Boolean {
        return geminiService.hasValidApiKey(userKey)
    }

    suspend fun generateContent(
        prompt: String,
        contentType: ContentType,
        tone: String = "Hype",
        platform: String = "Instagram & TikTok",
        imageUri: String? = null,
        photoPreset: String? = null,
        userGeminiKey: String? = null
    ): CreationItem {
        val hasKey = isGeminiConfigured(userGeminiKey)

        if (hasKey) {
            val systemInstruction = buildSystemPrompt(contentType, tone, platform, photoPreset)
            val result = geminiService.generateWithGemini(
                systemPrompt = systemInstruction,
                userPrompt = prompt,
                userKey = userGeminiKey
            )
            if (result.isSuccess) {
                val rawText = result.getOrNull().orEmpty()
                return parseGeminiResponse(rawText, prompt, contentType, tone, platform, imageUri)
            }
        }

        // Demo / Fallback smart viral engine (runs seamlessly with realistic delay)
        delay(1400) // realistic generation time for animations
        return generateSmartViralContent(prompt, contentType, tone, platform, imageUri, photoPreset)
    }

    private fun buildSystemPrompt(
        contentType: ContentType,
        tone: String,
        platform: String,
        photoPreset: String?
    ): String {
        return """
            You are VibeAI, the premier social media viral content engine of 2026.
            Your job is to generate high-retention, high-converting social media content.
            Target Platform: $platform
            Requested Tone: $tone
            Content Type: ${contentType.displayName}
            ${if (photoPreset != null) "Photo Studio Preset: $photoPreset" else ""}
            
            Structure your output cleanly with these labeled sections:
            [CAPTION]: The main high-energy post text
            [HOOKS]: 3 alternative scroll-stopping hooks (one per line starting with -)
            [CTA]: A punchy call to action
            [HASHTAGS]: 5 to 10 strategic hashtags
            ${if (contentType == ContentType.REEL_SCRIPT) """
            [15S_SCRIPT]: Complete 15-second timestamped script (0-3s Hook, 3-10s Core, 10-15s CTA)
            [30S_SCRIPT]: Complete 30-second timestamped script (0-3s Hook, 3-22s Value/Story, 22-30s Climax & CTA)
            """ else ""}
            [VIRAL_SCORE]: An estimated virality score out of 100 (e.g. 96)
        """.trimIndent()
    }

    private fun parseGeminiResponse(
        raw: String,
        prompt: String,
        contentType: ContentType,
        tone: String,
        platform: String,
        imageUri: String?
    ): CreationItem {
        var caption = ""
        val hooks = mutableListOf<String>()
        val hashtags = mutableListOf<String>()
        var cta = ""
        var s15 = ""
        var s30 = ""
        var viralScore = 93

        var currentSection = ""
        for (line in raw.lines()) {
            val trimmed = line.trim()
            when {
                trimmed.startsWith("[CAPTION]") -> {
                    currentSection = "CAPTION"
                    val rest = trimmed.removePrefix("[CAPTION]").trim(':').trim()
                    if (rest.isNotEmpty()) caption += "$rest\n"
                }
                trimmed.startsWith("[HOOKS]") -> currentSection = "HOOKS"
                trimmed.startsWith("[CTA]") -> {
                    currentSection = "CTA"
                    val rest = trimmed.removePrefix("[CTA]").trim(':').trim()
                    if (rest.isNotEmpty()) cta = rest
                }
                trimmed.startsWith("[HASHTAGS]") -> {
                    currentSection = "HASHTAGS"
                    val rest = trimmed.removePrefix("[HASHTAGS]").trim(':').trim()
                    if (rest.isNotEmpty()) {
                        hashtags.addAll(rest.split(" ").filter { it.startsWith("#") })
                    }
                }
                trimmed.startsWith("[15S_SCRIPT]") -> currentSection = "15S"
                trimmed.startsWith("[30S_SCRIPT]") -> currentSection = "30S"
                trimmed.startsWith("[VIRAL_SCORE]") -> {
                    val num = trimmed.filter { it.isDigit() }
                    viralScore = num.toIntOrNull()?.coerceIn(80, 99) ?: 94
                }
                else -> {
                    when (currentSection) {
                        "CAPTION" -> caption += "$line\n"
                        "HOOKS" -> if (trimmed.startsWith("-") || trimmed.startsWith("•") || (trimmed.firstOrNull()?.isDigit() == true)) {
                            hooks.add(trimmed.trimStart('-', '•', '0', '1', '2', '3', '4', '5', '.', ' ').trim())
                        }
                        "CTA" -> if (cta.isEmpty()) cta = trimmed
                        "HASHTAGS" -> {
                            hashtags.addAll(trimmed.split(" ").filter { it.startsWith("#") })
                        }
                        "15S" -> s15 += "$line\n"
                        "30S" -> s30 += "$line\n"
                    }
                }
            }
        }

        if (caption.isBlank()) {
            caption = raw
        }

        return CreationItem(
            title = prompt.take(30).ifBlank { "Viral Creation" },
            prompt = prompt,
            contentType = contentType,
            generatedContent = caption.trim(),
            hooks = if (hooks.isNotEmpty()) hooks else listOf("POV: You finally stopped doing this wrong...", "Stop scrolling if you want this result in 2026", "The 1 secret nobody in your niche is sharing"),
            hashtags = if (hashtags.isNotEmpty()) hashtags else listOf("#ViralGrowth", "#CreatorLife", "#TrendingNow", "#fyp", "#ExplorePage"),
            cta = if (cta.isNotBlank()) cta else "Save this for your next post & follow for daily drops 🚀",
            script15s = s15.trim(),
            script30s = s30.trim(),
            platform = platform,
            tone = tone,
            viralScore = viralScore,
            imageUri = imageUri
        )
    }

    private fun generateSmartViralContent(
        prompt: String,
        contentType: ContentType,
        tone: String,
        platform: String,
        imageUri: String?,
        photoPreset: String?
    ): CreationItem {
        val topic = prompt.ifBlank { "creating high-impact content effortlessly" }
        val randomScore = Random.nextInt(91, 99)

        return when (contentType) {
            ContentType.REEL_SCRIPT -> {
                val hooks = listOf(
                    "🔥 \"Stop scrolling if you want to master $topic before everyone else.\"",
                    "👀 \"The exact framework I used to 10x $topic in 7 days.\"",
                    "🤫 \"Nobody is talking about this $topic cheat code in 2026.\""
                )
                val s15 = """
                    [0:00 - 0:03] HOOK: Face camera with high energy. Fast zoom-in. Text on screen: "Don't do $topic until you watch this."
                    [0:03 - 0:10] VALUE: Show 2 quick screen recordings / B-roll clips. "Most people overcomplicate it. Here is the single shift that changes everything."
                    [0:10 - 0:15] CTA: Point down. "Save this reel so you don't lose it and drop a 🔥 in the comments!"
                """.trimIndent()

                val s30 = """
                    [0:00 - 0:03] HOOK: Holding phone, dramatic cut. "If you're still struggling with $topic, listen up for 30 seconds."
                    [0:03 - 0:12] THE MISTAKE: "Step 1 is stopping the rookie trap. 90% of people focus on the wrong variable."
                    [0:12 - 0:22] THE SOLUTION: "Instead, implement the 3-step loop: define your target, streamline the workflow, and let compounding do the work."
                    [0:22 - 0:30] CTA & OUTRO: "Send this to a friend who needs to hear it. Full breakdown linked in bio!"
                """.trimIndent()

                val caption = """
                    I used to think $topic was impossible until I simplified this exact framework 🤯

                    Here’s the unfiltered truth: success isn’t about grinding 24/7—it’s about having a repeatable playbook that cuts through the noise.

                    Breakdown:
                    1️⃣ Strip away what isn't converting
                    2️⃣ Focus 80% of energy on the core hook
                    3️⃣ Double down on what your audience actually saves

                    Which step are you testing first this week? Drop your thoughts below 👇
                """.trimIndent()

                CreationItem(
                    title = "Reel: $topic",
                    prompt = prompt,
                    contentType = ContentType.REEL_SCRIPT,
                    generatedContent = caption,
                    hooks = hooks,
                    hashtags = listOf("#ReelGrowth", "#ContentCreator", "#CreatorEconomy", "#ViralTips", "#AlgorithmHacks", "#ReelsViral"),
                    cta = "Save this reel & tag a creator who needs this mindset shift 📌",
                    script15s = s15,
                    script30s = s30,
                    platform = platform,
                    tone = tone,
                    viralScore = randomScore,
                    imageUri = imageUri
                )
            }

            ContentType.HASHTAGS -> {
                val tags = listOf(
                    "#Trending2026", "#GrowthMindset", "#AlgorithmSecrets",
                    "#SocialMediaStrategy", "#ViralContent", "#CreateEveryday",
                    "#TikTokTips", "#ReelIdeas", "#OrganicReach", "#ContentMarketing"
                )
                CreationItem(
                    title = "Hashtags for: $topic",
                    prompt = prompt,
                    contentType = ContentType.HASHTAGS,
                    generatedContent = tags.joinToString(" "),
                    hooks = listOf("Categorized for maximum reach: 3 Mega + 4 Niche + 3 Community tags"),
                    hashtags = tags,
                    cta = "Copy and paste into your first comment or end of caption.",
                    platform = platform,
                    tone = tone,
                    viralScore = randomScore,
                    imageUri = imageUri
                )
            }

            ContentType.BIO_GENERATOR -> {
                val bio = """
                    ⚡ Turning $topic into everyday magic
                    📈 Helping creators scale to 100k+ with AI workflows
                    👇 Claim your free viral playbook below!
                """.trimIndent()
                CreationItem(
                    title = "Bio for: $topic",
                    prompt = prompt,
                    contentType = ContentType.BIO_GENERATOR,
                    generatedContent = bio,
                    hooks = listOf(
                        "Authority Hook: Helping ambitious creators win at $topic 🚀",
                        "Minimalist Hook: $topic | building the future in public ✨",
                        "Story Hook: From zero to 100k+ using smart $topic strategies 👇"
                    ),
                    hashtags = listOf("#BioIdeas", "#CreatorProfile", "#PersonalBrand"),
                    cta = "Paste directly into your Instagram/TikTok profile bio.",
                    platform = platform,
                    tone = tone,
                    viralScore = randomScore,
                    imageUri = imageUri
                )
            }

            ContentType.STORY_GENERATOR -> {
                val story = """
                    Frame 1 (Poll/Sticker): "Be honest... do you struggle with $topic? [Yes / Not yet]"
                    Frame 2 (Behind-the-scenes): "I used to make this huge mistake until I discovered this workaround..."
                    Frame 3 (Value drop): "Here is the exact rule I follow every single morning 🧠"
                    Frame 4 (Link/DM trigger): "DM me 'VIBE' and I'll send you the full breakdown directly!"
                """.trimIndent()
                CreationItem(
                    title = "Story Flow: $topic",
                    prompt = prompt,
                    contentType = ContentType.STORY_GENERATOR,
                    generatedContent = story,
                    hooks = listOf("High-engagement 4-slide Instagram Story framework"),
                    hashtags = listOf("#StoryTemplates", "#InteractiveStory", "#StoryHacks"),
                    cta = "Use Instagram Poll or Slider sticker on Slide 1 to double reach.",
                    platform = platform,
                    tone = tone,
                    viralScore = randomScore,
                    imageUri = imageUri
                )
            }

            ContentType.AI_PHOTO_IDEA -> {
                val photoIdea = """
                    📸 Concept: Cinematic High-Contrast Aesthetic for "$topic"
                    
                    • Lighting: Golden hour directional sidelight or neon cyan/magenta rim glow.
                    • Composition: Low angle wide-shot (0.5x lens), subject placed along upper third.
                    • Props: Smartphone displaying futuristic graph, minimalist coffee cup, notebook.
                    • Posing: Mid-motion walking or thoughtful glance away from camera.
                    • Editing Vibe: Deep blacks, desaturated background with pop of warm vibrant highlights.
                """.trimIndent()
                CreationItem(
                    title = "Photo Concept: $topic",
                    prompt = prompt,
                    contentType = ContentType.AI_PHOTO_IDEA,
                    generatedContent = photoIdea,
                    hooks = listOf(
                        "Aesthetic editorial look that commands double-takes",
                        "Effortless candid vibe optimized for Instagram carousel cover",
                        "Bold contrast thumbnail that stops doom-scrolling"
                    ),
                    hashtags = listOf("#PhotoAesthetic", "#ContentCreation", "#PhotographyTips", "#InstaGood", "#VisualStorytelling"),
                    cta = "Shoot this in 4K 60fps or raw photo mode for maximum clarity.",
                    platform = platform,
                    tone = tone,
                    viralScore = randomScore,
                    imageUri = imageUri
                )
            }

            ContentType.PHOTO_STUDIO -> {
                val presetName = photoPreset ?: "AI Caption"
                val caption = when (presetName) {
                    "Profile Caption" -> "Living in the details. ⚡ Building something that speaks louder than words. $topic"
                    "Reel Caption" -> "POV: You finally stopped waiting for the right moment and just created it. Save this for when you need that spark. 🔥"
                    "Photo Description" -> "Rich cinematic scene captured in crisp natural tones. The composition highlights authentic textures, energetic ambient lighting, and an effortless modern aura around $topic."
                    "Photo Editing Prompt" -> "Color grade with film grain +15, shadows slightly lifted with teal undertone, warm orange skin tones, contrast ratio 1.2:1, subtle vignette."
                    "Hashtag Generator" -> "#VisualGram #AestheticAura #StreetStyle #CreatorMood #LensCulture #DailyInspo #ModernVibe #UrbanStory"
                    else -> "No filter needed when the energy matches the vision ✨ $topic is where momentum begins."
                }
                CreationItem(
                    title = "$presetName: $topic",
                    prompt = prompt,
                    contentType = ContentType.PHOTO_STUDIO,
                    generatedContent = caption,
                    hooks = listOf(
                        "\"Unapologetic energy.\" ✨",
                        "\"Found in my camera roll, kept in my head.\" 📸",
                        "\"If you were waiting for a sign, this is it.\" 💫"
                    ),
                    hashtags = listOf("#PhotoOfTheDay", "#VibeCheck", "#AestheticFeed", "#Visuals", "#GramVibes"),
                    cta = "Drop your favorite emoji if this matches your mood today 👇",
                    platform = platform,
                    tone = tone,
                    viralScore = randomScore,
                    imageUri = imageUri
                )
            }

            else -> { // VIRAL_CAPTION
                val caption = """
                    Stop doing $topic the old way. 2026 demands a whole new playbook ⚡️

                    Here’s the harsh reality: the algorithm didn’t get harder, the audience just got smarter. If you don't capture attention in the first 1.8 seconds, you’re invisible.

                    3 things to remember:
                    1. Clarity beats cleverness every single time.
                    2. If you don't feel a little uncomfortable posting it, it’s probably too safe.
                    3. Consistency isn't daily noise—it's high quality delivery.

                    Save this post before you write your next draft, and let me know in the comments: what’s your biggest hurdle with $topic right now? 💬
                """.trimIndent()

                CreationItem(
                    title = "Viral: $topic",
                    prompt = prompt,
                    contentType = ContentType.VIRAL_CAPTION,
                    generatedContent = caption,
                    hooks = listOf(
                        "POV: You finally cracked the code on $topic 👀",
                        "I tested 50 different methods for $topic. Here is what actually worked 🧪",
                        "Stop scrolling if you want to know the 1 rule everyone ignores 🚫"
                    ),
                    hashtags = listOf("#ViralCaptions", "#ContentHacks", "#CreatorTips", "#SocialMediaGrowth", "#2026Trends", "#GrowthStrategy"),
                    cta = "Double tap if you needed this reminder & share with a creator friend 🚀",
                    platform = platform,
                    tone = tone,
                    viralScore = randomScore,
                    imageUri = imageUri
                )
            }
        }
    }
}
