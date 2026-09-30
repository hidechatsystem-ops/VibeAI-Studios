package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VibeCyan
import com.example.ui.theme.VibeEmerald
import com.example.ui.theme.VibeMagenta

@Composable
fun ViralScoreBadge(
    score: Int,
    modifier: Modifier = Modifier
) {
    val (gradientColors, ratingText) = when {
        score >= 95 -> listOf(Color(0xFF10B981), Color(0xFF06B6D4)) to "Maximum Virality 🔥"
        score >= 90 -> listOf(Color(0xFF8B5CF6), Color(0xFFEC4899)) to "High Viral Potential ✨"
        else -> listOf(Color(0xFFF59E0B), Color(0xFFF97316)) to "Good Engagement 📈"
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x2210B981))
            .border(1.dp, Brush.horizontalGradient(gradientColors), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(gradientColors)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$score",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
            )
        }

        Column {
            Text(
                text = "VIRAL PREDICTION",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = VibeCyan,
                letterSpacing = 1.sp
            )
            Text(
                text = ratingText,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
