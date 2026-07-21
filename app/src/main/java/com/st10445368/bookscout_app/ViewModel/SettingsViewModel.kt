package com.st10445368.bookscout_app.ViewModel // Matches your import

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st10445368.bookscout_app.data.UserPreferencesRepository // Adjust if your repository folder is named differently
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // MUST be public so SettingsScreen can read viewModel.isDarkMode
    val isDarkMode: StateFlow<Boolean> = preferencesRepository.isDarkModeEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    // MUST be public so SettingsScreen can read viewModel.isGridView
    val isGridView: StateFlow<Boolean> = preferencesRepository.isGridViewPreferred
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setDarkModeEnabled(enabled)
        }
    }

    fun toggleGridView(isGrid: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setGridViewPreferred(isGrid)
        }
    }
}