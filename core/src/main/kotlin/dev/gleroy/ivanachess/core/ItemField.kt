package dev.gleroy.ivanachess.core

/**
 * Item field.
 */
interface ItemField {
    /**
     * Label.
     */
    val label: String

    /**
     * True if this field is sortable, false otherwise.
     */
    val isSortable: Boolean

    /**
     * True if this is searchable, false otherwise.
     */
    val isSearchable: Boolean
}
