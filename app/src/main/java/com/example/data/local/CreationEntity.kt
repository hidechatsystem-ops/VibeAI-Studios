package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ContentType
import com.example.data.model.CreationItem

@Entity(tableName = "creations")
data class CreationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val prompt: String,
    val contentType: String,
    val generatedContent: String,
    val hooksJson: String,
    val hashtagsJson: String,
    val cta: String,
    val script15s: String,
    val script30s: String,
    val platform: String,
    val tone: String,
    val viralScore: Int,
    val timestamp: Long,
    val isFavorite: Boolean,
    val imageUri: String?
) {
    fun toCreationItem(): CreationItem {
        val hooks = if (hooksJson.isBlank()) emptyList() else hooksJson.split("|||")
        val hashtags = if (hashtagsJson.isBlank()) emptyList() else hashtagsJson.split("|||")
        val type = try {
            ContentType.valueOf(contentType)
        } catch (_: Exception) {
            ContentType.VIRAL_CAPTION
        }
        return CreationItem(
            id = id,
            title = title,
            prompt = prompt,
            contentType = type,
            generatedContent = generatedContent,
            hooks = hooks,
            hashtags = hashtags,
            cta = cta,
            script15s = script15s,
            script30s = script30s,
            platform = platform,
            tone = tone,
            viralScore = viralScore,
            timestamp = timestamp,
            isFavorite = isFavorite,
            imageUri = imageUri
        )
    }

    companion object {
        fun fromCreationItem(item: CreationItem): CreationEntity {
            return CreationEntity(
                id = item.id,
                title = item.title,
                prompt = item.prompt,
                contentType = item.contentType.name,
                generatedContent = item.generatedContent,
                hooksJson = item.hooks.joinToString("|||"),
                hashtagsJson = item.hashtags.joinToString("|||"),
                cta = item.cta,
                script15s = item.script15s,
                script30s = item.script30s,
                platform = item.platform,
                tone = item.tone,
                viralScore = item.viralScore,
                timestamp = item.timestamp,
                isFavorite = item.isFavorite,
                imageUri = item.imageUri
            )
        }
    }
}
