package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContentType
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.VibeCyan
import com.example.ui.theme.VibeMagenta
import com.example.ui.theme.VibeVioletPrimary
import com.example.ui.viewmodel.VibeViewModel

@Composable
fun ReelScriptScreen(
    viewModel: VibeViewModel,
    onNavigateToCreator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topic by viewModel.reelTopic.collectAsState()
    val duration by viewModel.reelDuration.collectAsState()

    val durations = listOf("15s (Snappy)", "30s (Sweet Spot)", "60s (Deep Dive)")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Movie,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column {
                Text(
                    text = "Reel Script Studio",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "High-retention hooks, timestamped pacing & CTAs",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Topic Input Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "What is your Reel topic?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = topic,
                    onValueChange = { viewModel.reelTopic.value = it },
                    placeholder = { Text("e.g. '3 mindset shifts to stop procrastinating' or 'How I edit aesthetic reels'", fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("reel_topic_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VibeVioletPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Target Video Length",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    durations.forEach { d ->
                        val isSelected = duration == d
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.reelDuration.value = d },
                            label = { Text(d, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VibeMagenta,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Preset Topic Suggestions
        Text(
            text = "Trending Frameworks 🔥",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        val frameworks = listOf(
            "The 3-Second Problem vs Solution Framework",
            "Storytime: How I Lost Everything & Bounced Back",
            "The Step-by-Step 2026 AI Tool Tutorial",
            "Unpopular Opinion That Triggers Comment Debates"
        )

        frameworks.forEach { fw ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                cornerRadius = 12.dp,
                onClick = { viewModel.reelTopic.value = fw }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = fw, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text("Use ⚡", fontSize = 12.sp, color = VibeCyan, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Generate Reel Script Button
        NeonButton(
            text = "Generate Full Reel Script 🎬",
            onClick = {
                val topicText = if (topic.isNotBlank()) topic else "How to create viral content consistently"
                viewModel.generate(
                    prompt = topicText,
                    contentType = ContentType.REEL_SCRIPT,
                    onNavigateToCreator = onNavigateToCreator
                )
            },
            icon = Icons.Default.AutoAwesome,
            modifier = Modifier.fillMaxWidth(),
            testTag = "generate_reel_button"
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}
