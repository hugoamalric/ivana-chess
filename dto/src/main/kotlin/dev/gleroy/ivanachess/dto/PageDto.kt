package dev.gleroy.ivanachess.dto

/**
 * Page DTO.
 *
 * @param content Content.
 * @param number Current number.
 * @param totalItems Total number of elements.
 * @param totalPages Total number of pages.
 */
data class PageDto<T>(
    val content: List<T>,
    val number: Int,
    val totalItems: Int,
    val totalPages: Int
)
