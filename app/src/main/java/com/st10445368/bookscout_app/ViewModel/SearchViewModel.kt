package com.st10445368.bookscout_app.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st10445368.bookscout_app.data.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.st10445368.bookscout_app.model.Book
import com.st10445368.bookscout_app.ui.SearchUiState

class SearchViewModel(
    private val repository: BookRepository
) : ViewModel() {

    // Internal mutable list to keep track of accumulated items across pages
    private val cachedBooks = mutableListOf<Book>()

    private var currentQuery = ""
    private var currentPage = 1
    private var isLastPage = false

    // UI state backed by StateFlow for safe, lifecycle-aware collection in Compose
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    /**
     * Triggers a completely new book search. Resets pagination tracking entirely.
     */
    fun performSearch(query: String) {
        val trimmedQuery = query.trim()

        // Handle constraint: Empty search field
        if (trimmedQuery.isEmpty()) {
            _uiState.value = SearchUiState.Idle
            return
        }

        currentQuery = trimmedQuery
        currentPage = 1
        isLastPage = false
        cachedBooks.clear()

        _uiState.value = SearchUiState.Loading
        executeSearch()
    }

    /**
     * Loads the next page of results if a valid query exists and we aren't at the end.
     */
    fun loadNextPage() {
        // Prevent overlapping requests or loading past the API boundary
        if (_uiState.value is SearchUiState.Loading ||
            _uiState.value is SearchUiState.LoadingMore ||
            isLastPage ||
            currentQuery.isEmpty()
        ) return

        currentPage++
        _uiState.value = SearchUiState.LoadingMore
        executeSearch()
    }

    /**
     * Retries the failed request using the exact same page and query context.
     */
    fun retryLastRequest() {
        if (currentQuery.isEmpty()) return

        // If it was a pagination failure, keep LoadingMore state, else full screen loading
        if (currentPage > 1) {
            _uiState.value = SearchUiState.LoadingMore
        } else {
            _uiState.value = SearchUiState.Loading
        }
        executeSearch()
    }

    private fun executeSearch() {
        viewModelScope.launch {
            val result = repository.searchBooks(query = currentQuery, page = currentPage)

            result.onSuccess { newBooks ->
                if (newBooks.isEmpty()) {
                    if (currentPage == 1) {
                        // Handle constraint: No books found globally
                        _uiState.value = SearchUiState.Empty
                    } else {
                        // We hit the end of pagination stream
                        isLastPage = true
                        _uiState.value = SearchUiState.Success(
                            books = ArrayList(cachedBooks),
                            isLastPage = true
                        )
                    }
                } else {
                    // Open Library API returns pages of 20 elements by default.
                    // If it returns less than 20 elements, we've naturally hit the end.
                    if (newBooks.size < 20) {
                        isLastPage = true
                    }

                    cachedBooks.addAll(newBooks)
                    _uiState.value = SearchUiState.Success(
                        books = ArrayList(cachedBooks),
                        isLastPage = isLastPage
                    )
                }
            }.onFailure { exception ->
                // Keep the current page set back so a retry attempts the correct page number
                _uiState.value = SearchUiState.Error(
                    message = exception.message ?: "An unknown network error occurred."
                )
            }
        }
    }
}