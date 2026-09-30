package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ContentType
import com.example.ui.components.AIProcessingAnimation
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientCard
import com.example.ui.components.ViralScoreBadge
import com.example.ui.theme.VibeCyan
import com.example.ui.theme.VibeEmerald
import com.example.ui.theme.VibeMagenta
import com.example.ui.theme.VibeVioletPrimary
import com.example.ui.viewmodel.GenerationState
import com.example.ui.viewmodel.VibeViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreatorScreen(
    viewModel: VibeViewModel,
    onNavigateBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val genState by viewModel.generationState.collectAsState()
    val currentCreation by viewModel.currentCreation.collectAsState()

    var isEditing by remember { mutableStateOf(false) }
    var editableText by remember(currentCreation) { mutableStateOf(currentCreation?.generatedContent.orEmpty()) }

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = genState) {
            is GenerationState.Generating -> {
                AIProcessingAnimation(
                    stepText = state.step,
                    progress = state.progress,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is GenerationState.Error -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("⚠️", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Creation Paused",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.regenerate() },
                        colors = ButtonDefaults.buttonColors(containerColor = VibeVioletPrimary)
                    ) {
                        Text("Try Again")
                    }
                }
            }
            else -> {
                val item = currentCreation
                if (item == null) {
                    // Empty state
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("✨", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Generation Yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Head to the Home screen to enter a prompt and create viral content!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onNavigateBackToHome,
                            colors = ButtonDefaults.buttonColors(containerColor = VibeVioletPrimary)
                        ) {
                            Text("Go to Home")
                        }
                    }
                } else {
                    // Result Screen
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Top Bar: Format badge & Viral Score
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = VibeVioletPrimary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "${item.contentType.icon} ${item.contentType.displayName}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VibeVioletPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = VibeCyan.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = item.platform,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = VibeCyan,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            ViralScoreBadge(score = item.viralScore)
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // If image was attached, show photo thumbnail
                        if (!item.imageUri.isNullOrBlank()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 14.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AsyncImage(
                                    model = item.imageUri,
                                    contentDescription = "Generated for photo",
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Column {
                                    Text(
                                        text = "Photo Reference",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Tone: ${item.tone}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Main Content Card
                        GradientCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (item.contentType == ContentType.REEL_SCRIPT) "Reel Caption & Summary" else "Generated Result",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = VibeCyan
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        // Edit Toggle Button
                                        IconButton(
                                            onClick = {
                                                if (isEditing) {
                                                    viewModel.updateGeneratedContent(editableText)
                                                    isEditing = false
                                                } else {
                                                    editableText = item.generatedContent
                                                    isEditing = true
                                                }
                                            },
                                            modifier = Modifier.testTag("edit_button")
                                        ) {
                                            Icon(
                                                imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                                                contentDescription = if (isEditing) "Save edit" else "Edit content",
                                                tint = if (isEditing) VibeEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        // Quick Copy Button
                                        IconButton(
                                            onClick = { viewModel.copyToClipboard(context, item.generatedContent) },
                                            modifier = Modifier.testTag("copy_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copy text",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                if (isEditing) {
                                    OutlinedTextField(
                                        value = editableText,
                                        onValueChange = { editableText = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                } else {
                                    Text(
                                        text = item.generatedContent,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 24.sp
                                    )
                                }
                            }
                        }

                        // Reel Scripts Breakdown if ContentType is REEL_SCRIPT
                        if (item.contentType == ContentType.REEL_SCRIPT) {
                            Spacer(modifier = Modifier.height(16.dp))

                            // 15-Second Script Card
                            if (item.script15s.isNotBlank()) {
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "⏱️ 15-Second Script (Fast Paced)",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = VibeMagenta
                                            )
                                            IconButton(onClick = { viewModel.copyToClipboard(context, item.script15s) }) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy 15s script", tint = VibeMagenta)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = item.script15s, fontSize = 13.sp, lineHeight = 20.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            // 30-Second Script Card
                            if (item.script30s.isNotBlank()) {
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "⏱️ 30-Second Script (High Value)",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = VibeCyan
                                            )
                                            IconButton(onClick = { viewModel.copyToClipboard(context, item.script30s) }) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy 30s script", tint = VibeCyan)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = item.script30s, fontSize = 13.sp, lineHeight = 20.sp)
                                    }
                                }
                            }
                        }

                        // Alternative Hooks Section
                        if (item.hooks.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(18.dp))
                            Text(
                                text = "Alternative Scroll-Stopping Hooks 🔥",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            item.hooks.forEachIndexed { index, hook ->
                                GlassCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    cornerRadius = 14.dp,
                                    onClick = { viewModel.copyToClipboard(context, hook, "Hook") }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = hook,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy hook",
                                            tint = VibeVioletPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Strategic Hashtags
                        if (item.hashtags.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(18.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Strategic Hashtags 🏷️",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Copy All",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibeCyan,
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.copyToClipboard(context, item.hashtags.joinToString(" "), "Hashtags")
                                        }
                                        .padding(4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                item.hashtags.forEach { tag ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.clickable {
                                            viewModel.copyToClipboard(context, tag, "Hashtag")
                                        }
                                    ) {
                                        Text(
                                            text = tag,
                                            fontSize = 12.sp,
                                            color = VibeCyan,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Call To Action Card
                        if (item.cta.isNotBlank()) {
                            Spacer(modifier = Modifier.height(18.dp))
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Call-To-Action (CTA)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = VibeMagenta
                                        )
                                        Text(
                                            text = item.cta,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    IconButton(onClick = { viewModel.copyToClipboard(context, item.cta, "CTA") }) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy CTA", tint = VibeMagenta)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // Full Action Buttons Row: Copy, Regenerate, Edit, Share, Save
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Copy All Button
                            OutlinedButton(
                                onClick = {
                                    val full = buildString {
                                        appendLine(item.generatedContent)
                                        if (item.cta.isNotBlank()) appendLine("\n${item.cta}")
                                        if (item.hashtags.isNotEmpty()) appendLine("\n${item.hashtags.joinToString(" ")}")
                                    }
                                    viewModel.copyToClipboard(context, full, "Full Post")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("copy_all_button"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy", fontSize = 13.sp)
                            }

                            // Regenerate Button
                            OutlinedButton(
                                onClick = { viewModel.regenerate() },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("regenerate_button"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Retry", fontSize = 13.sp)
                            }

                            // Share Button
                            Button(
                                onClick = {
                                    val full = buildString {
                                        appendLine(item.generatedContent)
                                        if (item.cta.isNotBlank()) appendLine("\n${item.cta}")
                                        if (item.hashtags.isNotEmpty()) appendLine("\n${item.hashtags.joinToString(" ")}")
                                    }
                                    viewModel.shareContent(context, full)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("share_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = VibeCyan)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Share", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            // Save / Favorite Button
                            IconButton(
                                onClick = { viewModel.toggleFavorite(item) },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .testTag("save_button")
                            ) {
                                Icon(
                                    imageVector = if (item.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Save creation",
                                    tint = if (item.isFavorite) VibeMagenta else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }
    }
}
