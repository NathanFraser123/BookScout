package com.st10445368.bookscout_app

import com.st10445368.bookscout_app.data.BookRepository
import com.st10445368.bookscout_app.data.OpenLibraryApiService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class BookRepositoryTest {

    private lateinit var repository: BookRepository
    private val mockApiService = mock(OpenLibraryApiService::class.java)

    @Before
    fun setUp() {
        repository = BookRepository(mockApiService)
    }

    // =========================================================================
    // Normalise Query Tests
    // =========================================================================

    @Test
    fun normaliseQuery_trimsWhitespace() {
        val result = repository.normaliseQuery("   clean code   ")

        assertEquals("clean code", result)
    }

    @Test
    fun normaliseQuery_emptyString_returnsEmptyString() {
        val result = repository.normaliseQuery("")

        assertEquals("", result)
    }

    @Test
    fun normaliseQuery_newlinesAndTabs_trimsWhitespace() {
        val result = repository.normaliseQuery("\n\t kotlin multiplatform \t\n")

        assertEquals("kotlin multiplatform", result)
    }

    // =========================================================================
    // Is Valid Query Tests
    // =========================================================================

    @Test
    fun isValidQuery_validTitle_returnsTrue() {
        val result = repository.isValidQuery("  Android Development  ")

        assertTrue(result)
    }

    @Test
    fun isValidQuery_onlySpaces_returnsFalse() {
        val result = repository.isValidQuery(" ")

        assertFalse(result)
    }

    @Test
    fun isValidQuery_emptyString_returnsFalse() {
        val result = repository.isValidQuery("")

        assertFalse(result)
    }

    @Test
    fun isValidQuery_singleCharacter_returnsTrue() {
        val result = repository.isValidQuery(" a ")

        assertTrue(result)
    }
}