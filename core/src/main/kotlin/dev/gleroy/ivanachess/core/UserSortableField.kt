package dev.gleroy.ivanachess.core

/**
 * User sortable field.
 *
 * @param label Label.
 */
enum class UserSortableField(
    override val label: String,
) : SortableEntityField<User> {
    /**
     * Email.
     */
    Email("email"),

    /**
     * Pseudo.
     */
    Pseudo("pseudo")
}
