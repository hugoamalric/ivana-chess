package dev.gleroy.ivanachess.core

/**
 * Common entity field.
 *
 * @param label Label.
 */
enum class CommonEntityField(
    override val label: String,
) : ItemField {
    /**
     * ID.
     */
    Id("id"),

    /**
     * Creation date.
     */
    CreationDate("creationDate");

    override val isSortable get() = true

    override val isSearchable get() = false
}
