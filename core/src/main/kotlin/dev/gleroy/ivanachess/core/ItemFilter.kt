package dev.gleroy.ivanachess.core

/**
 * Item filter.
 *
 * @param field Field.
 * @param value Value.
 */
data class ItemFilter(
    val field: ItemField,
    val value: String,
)
