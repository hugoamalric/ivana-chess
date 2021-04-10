package dev.gleroy.ivanachess.io

/**
 * Representation of page.
 *
 * @param content Content.
 * @param number Current number.
 * @param totalItems Total number of items.
 * @param totalPages Total number of pages.
 */
data class PageRepresentation<T : Representation>(
    val content: List<T>,
    val number: Int,
    val totalItems: Int,
    val totalPages: Int,
) : Representation
