package dev.gleroy.ivanachess.core

/**
 * User field.
 *
 * @param label Label.
 * @param isSortable True if this field is sortable, false otherwise.
 * @param isFilterable True if this field is filterable, false otherwise.
 * @param isSearchable True if this field is searchable, false otherwise.
 */
enum class UserField(
    override val label: String,
    override val isSortable: Boolean,
    override val isFilterable: Boolean,
    override val isSearchable: Boolean,
) : ItemField {
    /**
     * Email.
     */
    Email("email", true, false, true),

    /**
     * Pseudo.
     */
    Pseudo("pseudo", true, true, true),

    /**
     * Role.
     */
    Role("role", false, true, false);
}
