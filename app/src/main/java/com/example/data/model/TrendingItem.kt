package com.example.data.model

data class TrendingCategory(
    val id: String,
    val name: String,
    val icon: String,
    val tagCount: String
)

data class TrendingHook(
    val id: String,
    val hookText: String,
    val category: String,
    val viralRate: String,
    val explanation: String
)

data class TrendingHashtag(
    val tag: String,
    val postCount: String,
    val growthRate: String,
    val category: String
)

data class TrendingReelIdea(
    val id: String,
    val title: String,
    val format: String,
    val audioTrend: String,
    val hookSuggestion: String,
    val whyItWorks: String,
    val viralScore: Int
)
