package com.st10445368.bookscout_app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

// Type-safe destinations
@Serializable object SearchRoute
@Serializable object SettingsRoute

@Serializable
data class DetailRoute(val bookId: String)

// Structured object to map destinations to the Bottom Bar UI elements
sealed class BottomBarItem<T : Any>(
    val route: T,
    val title: String,
    val icon: ImageVector
) {
    object Search : BottomBarItem<SearchRoute>(SearchRoute, "Search", Icons.Default.Search)
    object Settings : BottomBarItem<SettingsRoute>(SettingsRoute, "Settings", Icons.Default.Settings)
}

val bottomNavItems = listOf(
    BottomBarItem.Search,
    BottomBarItem.Settings
)