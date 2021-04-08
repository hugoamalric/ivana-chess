package dev.gleroy.ivanachess.core

/**
 * Page.
 *
 * @param content Content.
 * @param number Current number.
 * @param totalItems Total number of elements.
 * @param totalPages Total number of pages.
 */
data class Page<T>(
    val content: List<T> = emptyList(),
    val number: Int = 1,
    val totalItems: Int,
    val totalPages: Int
)
