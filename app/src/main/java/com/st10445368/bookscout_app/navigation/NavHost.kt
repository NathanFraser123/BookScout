package com.st10445368.bookscout_app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.st10445368.bookscout_app.ui.Screens.SearchScreen
import com.st10445368.bookscout_app.ui.Screens.SettingsScreen


@Composable
fun MainAppContainer() {
    val navController = rememberNavController()

    // Track the current back stack entry to see which destination is active
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    // Correctly check if the current route tree matches the destination
                    val isSelected = currentDestination?.hierarchy?.any {
                        it.hasRoute(item.route::class)
                    } == true

                    NavigationBarItem(
                        selected = isSelected,
                        label = { Text(item.title) },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                        onClick = {
                            navController.navigate(item.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a huge stack of destinations
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // The NavHost controls what screen file gets loaded inside the content area
        NavHost(
            navController = navController,
            startDestination = SearchRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<SearchRoute> {
                SearchScreen(
                    onBookClick = { bookId ->
                        // Future: Navigate to detail screen passing the bookId
                        // navController.navigate(DetailRoute(bookId))
                    }
                )
            }

            composable<SettingsRoute> {
                SettingsScreen()
            }
        }
    }
}