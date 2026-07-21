package com.st10445368.bookscout_app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.st10445368.bookscout_app.ViewModel.SearchViewModel
import com.st10445368.bookscout_app.ViewModel.SettingsViewModel
import com.st10445368.bookscout_app.ui.Screens.DetailScreen
import com.st10445368.bookscout_app.ui.Screens.SearchScreen
import com.st10445368.bookscout_app.ui.Screens.SettingsScreen
import com.st10445368.bookscout_app.ui.SearchUiState

@Composable
fun MainAppContainer(
    searchViewModel: SearchViewModel,
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Hide bottom bar when viewing the Details Screen
    val isDetailScreen = currentDestination?.hierarchy?.any {
        it.hasRoute(DetailRoute::class)
    } == true

    Scaffold(
        bottomBar = {
            if (!isDetailScreen) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any {
                            it.hasRoute(item.route::class)
                        } == true

                        NavigationBarItem(
                            selected = isSelected,
                            label = { Text(item.title) },
                            icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SearchRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Search Screen Route
            composable<SearchRoute> {
                SearchScreen(
                    viewModel = searchViewModel,
                    onBookClick = { bookId ->
                        navController.navigate(DetailRoute(bookId))
                    }
                )
            }

            // Settings Screen Route
            composable<SettingsRoute> {
                SettingsScreen(viewModel = settingsViewModel)
            }

            // Book Details Screen Route
            composable<DetailRoute> { backStackEntry ->
                val route: DetailRoute = backStackEntry.toRoute()

                // Retrieve the matching book from the search ViewModel state
                val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()
                val selectedBook = (uiState as? SearchUiState.Success)?.books?.find { it.id == route.bookId }

                DetailScreen(
                    book = selectedBook,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}