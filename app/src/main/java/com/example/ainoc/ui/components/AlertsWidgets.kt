package com.example.ainoc.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ainoc.data.model.Comment
import com.example.ainoc.data.model.TimelineEvent
import com.example.ainoc.ui.theme.*

// Note: The FilterChip from the main AlertsScreen.kt is used directly.
// This file contains the other helper widgets for the details screen.

@Composable
fun TimelineNode(event: TimelineEvent) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (!event.isFirst) {
                Divider(Modifier.height(16.dp).width(2.dp), color = PrimaryPurple.copy(alpha = 0.5f))
            } else {
                Spacer(Modifier.height(16.dp))
            }
            Icon(Icons.Default.Circle, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(12.dp))
            if (!event.isLast) {
                Divider(Modifier.fillMaxHeight().width(2.dp), color = PrimaryPurple.copy(alpha = 0.5f))
            }
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.padding(bottom = if (!event.isLast) 32.dp else 0.dp)) {
            Text(event.timestamp, fontWeight = FontWeight.Bold, color = AccentBeige)
            Text(event.description, color = AccentBeige.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun EvidenceCard(title: String, icon: @Composable () -> Unit, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon()
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, color = AccentBeige)
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BackgroundDark)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(comment.author, fontWeight = FontWeight.Bold, color = PrimaryPurple)
                Text(comment.timestamp, style = MaterialTheme.typography.bodySmall, color = AccentBeige.copy(alpha = 0.6f))
            }
            Spacer(Modifier.height(4.dp))
            Text(comment.text, color = AccentBeige.copy(alpha = 0.9f))
        }
    }
}