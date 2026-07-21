package com.st10445368.bookscout_app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Initialize DataStore instance on Context
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
        val GRID_VIEW_PREFERRED = booleanPreferencesKey("grid_view_preferred")
    }

    //Theme preference loaded
    val isDarkModeEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DARK_MODE_ENABLED] ?: false
    }

    //layout style logic
    val isGridViewPreferred: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.GRID_VIEW_PREFERRED] ?: true
    }

    // dark mode toggle
    suspend fun setDarkModeEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE_ENABLED] = enabled
        }
    }

    // list or card toggle
    suspend fun setGridViewPreferred(isGrid: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.GRID_VIEW_PREFERRED] = isGrid
        }
    }
}