package com.example.ainoc.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

// This is a blueprint for a single button on the bottom navigation bar.
// It holds all the info needed to draw one tab, like its icon, label, and where it goes.
data class BottomNavItem(
    val title: String,                // The text label (e.g., "Dashboard")
    val route: String,                // The destination ID this button links to
    val selectedIcon: ImageVector,    // The icon to show when this tab is active
    val unselectedIcon: ImageVector,  // The icon to show when this tab is inactive
    val hasBadge: Boolean,            // If true, a red dot appears on the icon
    val badgeCount: Int? = null       // If set, a number appears in the red dot (e.g., "3 alerts")
)