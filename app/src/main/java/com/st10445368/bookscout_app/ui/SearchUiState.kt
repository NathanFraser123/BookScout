package com.st10445368.bookscout_app.ui

import com.st10445368.bookscout_app.model.Book

sealed interface SearchUiState {
    object Idle : SearchUiState
    object Loading : SearchUiState
    object LoadingMore : SearchUiState
    data class Success(val books: List<Book>, val isLastPage: Boolean) : SearchUiState
    data class Error(val message: String) : SearchUiState
    object Empty : SearchUiState
}