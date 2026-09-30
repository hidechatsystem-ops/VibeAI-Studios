package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ContentType
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.VibeCyan
import com.example.ui.theme.VibeMagenta
import com.example.ui.theme.VibeVioletPrimary
import com.example.ui.viewmodel.VibeViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: VibeViewModel,
    onNavigateToCreator: () -> Unit,
    onNavigateToTrending: () -> Unit,
    onNavigateToStudio: () -> Unit,
    onStartVoice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val prompt by viewModel.promptInput.collectAsState()
    val selectedType by viewModel.selectedContentType.collectAsState()
    val selectedTone by viewModel.selectedTone.collectAsState()
    val selectedPlatform by viewModel.selectedPlatform.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()

    // Android Zero-Permission Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onPhotoSelected(uri)
        }
    }

    val tones = listOf("Hype", "Witty", "Aesthetic", "Professional", "Casual", "Savage")
    val platforms = listOf("Instagram & TikTok", "Reels & Shorts", "X / Twitter", "LinkedIn", "Story")

    val quickActions = listOf(
        ContentType.VIRAL_CAPTION,
        ContentType.AI_PHOTO_IDEA,
        ContentType.REEL_SCRIPT,
        ContentType.HASHTAGS,
        ContentType.STORY_GENERATOR,
        ContentType.BIO_GENERATOR
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Brand Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚡", fontSize = 20.sp)
                }
                Column {
                    Text(
                        text = "VibeAI",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Turn ideas into viral reach",
                        style = MaterialTheme.typography.bodySmall,
                        color = VibeCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onNavigateToTrending)
                    .testTag("trending_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = "Trending",
                        tint = VibeMagenta,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "What's Hot",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Headline
        Text(
            text = "Create Something Viral ✨",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Turn a quick thought, photo, or topic into high-converting social content in seconds.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 20.dp)
        )

        // Main Input Glass Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = MaterialTheme.colorScheme.outline
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // If photo is loaded, show a banner
                if (selectedImageUri != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, VibeCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Attached photo",
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Column {
                                Text(
                                    text = "Photo Attached 📸",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Will generate image-tailored copy",
                                    fontSize = 11.sp,
                                    color = VibeCyan
                                )
                            }
                        }
                        IconButton(onClick = { viewModel.onPhotoSelected(null) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Remove attached photo",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = prompt,
                    onValueChange = { viewModel.promptInput.value = it },
                    placeholder = {
                        Text(
                            "e.g., '5 secrets to grow a personal brand without showing your face' or 'My new aesthetic room setup'",
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("prompt_input"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VibeVioletPrimary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom row inside card: Mic & Photo upload buttons + Clear
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Microphone Button
                        Surface(
                            shape = CircleShape,
                            color = VibeVioletPrimary.copy(alpha = 0.15f),
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onStartVoice)
                                .testTag("mic_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Voice Input",
                                    tint = VibeVioletPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Photo Picker Button
                        Surface(
                            shape = CircleShape,
                            color = VibeCyan.copy(alpha = 0.15f),
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                                .testTag("image_upload_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = "Attach Photo",
                                    tint = VibeCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    if (prompt.isNotBlank()) {
                        IconButton(onClick = { viewModel.promptInput.value = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Prompt",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Actions Section
        Text(
            text = "Quick Formats",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickActions.forEach { type ->
                val isSelected = selectedType == type
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectedContentType.value = type },
                    label = {
                        Text("${type.icon} ${type.displayName}", fontSize = 13.sp)
                    },
                    modifier = Modifier.testTag("action_${type.name.lowercase()}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VibeVioletPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Tone Selector Row
        Text(
            text = "Vibe & Tone",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tones.forEach { tone ->
                val isSelected = selectedTone == tone
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectedTone.value = tone },
                    label = { Text(tone, fontSize = 12.sp) },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VibeCyan,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Platform Selector Row
        Text(
            text = "Target Destination",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            platforms.forEach { platform ->
                val isSelected = selectedPlatform == platform
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectedPlatform.value = platform },
                    label = { Text(platform, fontSize = 12.sp) },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VibeMagenta,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Primary Action: "Create with AI ✨"
        NeonButton(
            text = "Create with AI ✨",
            onClick = {
                viewModel.generate(
                    onNavigateToCreator = onNavigateToCreator
                )
            },
            icon = Icons.Default.AutoAwesome,
            modifier = Modifier.fillMaxWidth(),
            testTag = "generate_button"
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}
