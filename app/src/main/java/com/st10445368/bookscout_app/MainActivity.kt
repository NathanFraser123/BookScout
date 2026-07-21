package com.st10445368.bookscout_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.st10445368.bookscout_app.data.BookRepository
import com.st10445368.bookscout_app.data.OpenLibraryApiService
import com.st10445368.bookscout_app.data.UserPreferencesRepository
import com.st10445368.bookscout_app.ViewModel.SearchViewModel
import com.st10445368.bookscout_app.ViewModel.SettingsViewModel
import com.st10445368.bookscout_app.navigation.MainAppContainer

import com.st10445368.bookscout_app.ui.theme.BookScout_AppTheme
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Build OkHttpClient with 15-second timeouts
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        // 2. Build Json parser that handles unexpected fields from Open Library
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        // 3. Initialize Retrofit & OpenLibraryApiService
        val retrofit = Retrofit.Builder()
            .baseUrl("https://openlibrary.org/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        val apiService = retrofit.create(OpenLibraryApiService::class.java)

        // 4. Initialize Repositories
        val bookRepository = BookRepository(apiService)
        val userPreferencesRepository = UserPreferencesRepository(applicationContext)

        // 5. Build ViewModel Factories
        val searchViewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchViewModel(bookRepository) as T
            }
        }

        val settingsViewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(userPreferencesRepository) as T
            }
        }

        setContent {
            // Instantiate ViewModels using the custom factories
            val searchViewModel: SearchViewModel = viewModel(factory = searchViewModelFactory)
            val settingsViewModel: SettingsViewModel = viewModel(factory = settingsViewModelFactory)

            // Observe dark theme preference from DataStore
            val isDarkMode by settingsViewModel.isDarkMode.collectAsStateWithLifecycle()

            BookScout_AppTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Launch main screen with full bottom navigation & search API support!
                    MainAppContainer(
                        searchViewModel = searchViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}