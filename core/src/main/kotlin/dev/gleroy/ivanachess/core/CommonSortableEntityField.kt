package dev.gleroy.ivanachess.core

/**
 * Common sortable field.
 *
 * @param label Label.
 */
enum class CommonSortableEntityField(
    override val label: String,
) : SortableEntityField<Nothing> {
    /**
     * ID.
     */
    Id("id"),

    /**
     * Creation date.
     */
    CreationDate("creationDate")
}
