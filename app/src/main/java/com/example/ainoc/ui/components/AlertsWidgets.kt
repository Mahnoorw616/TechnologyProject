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

// This file contains small, reusable UI parts specifically for the "Alert Details" screen.

// This draws a single step in the vertical timeline (the line with dots on the left).
// It connects events visually to show the sequence of what happened.
@Composable
fun TimelineNode(event: TimelineEvent) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Draw the line above the dot (unless it's the very first event).
            if (!event.isFirst) {
                Divider(Modifier.height(16.dp).width(2.dp), color = PrimaryPurple.copy(alpha = 0.5f))
            } else {
                Spacer(Modifier.height(16.dp))
            }
            // Draw the dot representing this event.
            Icon(Icons.Default.Circle, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(12.dp))
            // Draw the line below the dot (unless it's the very last event).
            if (!event.isLast) {
                Divider(Modifier.fillMaxHeight().width(2.dp), color = PrimaryPurple.copy(alpha = 0.5f))
            }
        }
        Spacer(Modifier.width(16.dp))
        // Draw the text (Time and Description) next to the timeline.
        Column(Modifier.padding(bottom = if (!event.isLast) 32.dp else 0.dp)) {
            Text(event.timestamp, fontWeight = FontWeight.Bold, color = AccentBeige)
            Text(event.description, color = AccentBeige.copy(alpha = 0.8f))
        }
    }
}

// This creates a standard card layout for sections like "AI Insight" or "Recommendations".
// It gives them a consistent background, rounded corners, and a title with an icon.
@Composable
fun EvidenceCard(title: String, icon: @Composable () -> Unit, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon() // The icon passed in (like a lightbulb)
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, color = AccentBeige)
            }
            Spacer(Modifier.height(12.dp))
            content() // The actual text or data passed in to be displayed inside the card.
        }
    }
}

// This draws a single user comment in a chat-bubble style.
// It shows who wrote it, when, and what they said.
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