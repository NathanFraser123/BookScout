package com.st10445368.bookscout_app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val publishYear: String,
    val coverUrl: String?,
    val isbn: String
)

@Serializable
data class OpenLibrarySearchResponse(
    @SerialName("docs") val docs: List<BookDto>
)

@Serializable
data class BookDto(
    @SerialName("key") val key: String,
    @SerialName("title") val title: String,
    @SerialName("author_name") val authorName: List<String>? = null,
    @SerialName("first_publish_year") val firstPublishYear: Int? = null,
    @SerialName("cover_i") val coverI: Int? = null,
    @SerialName("isbn") val isbn: List<String>? = null
)

// Extension function to safely handle missing fields from the API
fun BookDto.toDomain(): Book {
    return Book(
        id = key.replace("/works/", ""),
        title = title,
        author = authorName?.firstOrNull() ?: "Unknown Author",
        publishYear = firstPublishYear?.toString() ?: "Year Unknown",
        coverUrl = coverI?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" },
        isbn = isbn?.firstOrNull() ?: ""
    )
}