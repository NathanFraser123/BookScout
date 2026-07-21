package com.st10445368.bookscout_app.data

import retrofit2.http.GET
import retrofit2.http.Query
import com.st10445368.bookscout_app.data.OpenLibraryApiService
import com.st10445368.bookscout_app.model.OpenLibrarySearchResponse

interface OpenLibraryApiService {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int = 20
    ): OpenLibrarySearchResponse
}