package com.example.ainoc.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data class representing an item in the bottom navigation bar.
 */
data class BottomNavItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasBadge: Boolean,
    val badgeCount: Int? = null
)

