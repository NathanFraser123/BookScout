package com.st10445368.bookscout_app.data

import android.os.Build
import androidx.annotation.RequiresExtension
import com.st10445368.bookscout_app.model.Book
import com.st10445368.bookscout_app.ui.SearchUiState
import java.io.IOException
import com.st10445368.bookscout_app.model.toDomain
import com.st10445368.bookscout_app.model.OpenLibrarySearchResponse
import com.st10445368.bookscout_app.model.BookDto
import retrofit2.HttpException

class BookRepository(private val apiService: OpenLibraryApiService) {

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend fun searchBooks(query: String, page: Int): Result<List<Book>> {
        return try {
            val response = apiService.searchBooks(query = query, page = page)
            val domainBooks = response.docs.map { it.toDomain() }
            Result.success(domainBooks)
        } catch (e: IOException) {
            Result.failure(Exception("No internet connection or server timeout."))
        } catch (e: HttpException) {
            Result.failure(Exception("Server error: ${e.code()}. Please try again later."))
        } catch (e: Exception) {
            Result.failure(Exception("An unexpected error occurred."))
        }
    }
}

// Note: You will also need the OpenLibraryApiService interface we mentioned earlier
// for this file to recognize "OpenLibraryApiService". Let me know if you need that snippet pasted here too!